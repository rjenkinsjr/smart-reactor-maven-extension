/*
 * Copyright (C) 2016 Ronald Jack Jenkins Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.ronjenkins.maven.rtr;

import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.graph.DefaultProjectDependencyGraph;
import org.apache.maven.project.DuplicateProjectException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.DefaultReleaseEnvironment;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.ReleasePhase;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.ExceptionUtils;
import org.codehaus.plexus.util.dag.CycleDetectedException;

/**
 * The entry point for the Smart Reactor Maven Extension.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "rtr")
public class RTR extends AbstractMavenLifecycleParticipant {

    private static final String PROP_DISABLED = "rtr.disabled";
    private static final boolean PROPDEFAULT_DISABLED = false;
    private static final String PROP_SINGLE_POM_ONLY_REACTOR_ALLOWED = "rtr.allowSinglePomOnlyReactor";
    private static final boolean PROPDEFAULT_SINGLE_POM_ONLY_REACTOR_ALLOWED = false;
    private static final String PROP_RELEASE = "rtr.release";
    private static final boolean PROPDEFAULT_RELEASE = false;

    @Requirement
    private Logger logger;

    @Requirement
    private ProjectBuilder builder;

    private List<String> releasePhases;
    private List<String> rollbackPhases;

    @Requirement(role = ReleasePhase.class)
    private Map<String, ReleasePhase> availablePhases;

    /**
     * RTR entry point.
     * 
     * @param session
     *            the current Maven session, never null.
     */
    @Override
    public void afterProjectsRead(final MavenSession session)
            throws MavenExecutionException {
        // Was RTR requested?
        final boolean disabled = Util.getBooleanProperty(PROP_DISABLED,
                PROPDEFAULT_DISABLED, session, null);
        if (disabled) {
            return;
        }
        // Announce RTR.
        logger.info("Assembling smart reactor...");
        // Identify the execution root.
        final MavenProject executionRoot = session.getTopLevelProject();
        // Remove from the reactor all non-SNAPSHOT projects.
        final List<MavenProject> projects = session.getProjects();
        final Iterator<MavenProject> iterator = projects.iterator();
        MavenProject project;
        while (iterator.hasNext()) {
            project = iterator.next();
            if (!project.getArtifact().isSnapshot()) {
                iterator.remove();
            }
        }
        // Sanity checks.
        if (projects.isEmpty()) {
            throw new MavenExecutionException(
                    "Smart reactor sanity check failed:",
                    new SmartReactorSanityCheckException(
                            "Reactor is empty. Did you forget to bump one of your projects to a SNAPSHOT version?"));
        }
        if (!projects.contains(executionRoot)) {
            throw new MavenExecutionException(
                    "Smart reactor sanity check failed:",
                    new SmartReactorSanityCheckException(
                            "Reactor does not contain execution root project. Did you forget to bump its version to a SNAPSHOT?"));
        }
        // If the reactor is composed of a single POM-only project, and if this
        // type of reactor is not permitted, fail.
        final boolean isPomOnly = projects.size() == 1
                && executionRoot.getArtifact().getType().equals("pom");
        if (isPomOnly) {
            final boolean singlePomOnlyReactorAllowed = Util
                    .getBooleanProperty(PROP_SINGLE_POM_ONLY_REACTOR_ALLOWED,
                            PROPDEFAULT_SINGLE_POM_ONLY_REACTOR_ALLOWED,
                            session, executionRoot);
            if (!singlePomOnlyReactorAllowed) {
                throw new MavenExecutionException(
                        "Smart reactor sanity check failed:",
                        new SmartReactorSanityCheckException(
                                "Reactor contains a single pom-packaging project, which is not allowed. If this is intended, set property \""
                                        + PROP_SINGLE_POM_ONLY_REACTOR_ALLOWED
                                        + "\" to true."));
            }
        }
        // Do a release, if it was requested.
        if (Util.getBooleanProperty(PROP_RELEASE, PROPDEFAULT_RELEASE, session,
                null)) {
            updateReactorWithReleases(session);
        }
        // Recalculate the reactor dependency graph.
        try {
            session.setProjectDependencyGraph(new DefaultProjectDependencyGraph(
                    projects));
        } catch (final CycleDetectedException | DuplicateProjectException e) {
            throw new MavenExecutionException(
                    "Could not assemble new project dependency graph", e);
        }
        // Done. Maven build will proceed from here, none the wiser. ;)
    }

    /**
     * Updates the reactor for this Maven session so that release projects are
     * built instead of SNAPSHOT projects.
     * 
     * @param session
     *            not null.
     * @throws MavenExecutionException
     *             if the execution of any release phase fails.
     */
    private void updateReactorWithReleases(final MavenSession session)
            throws MavenExecutionException {
        logger.info("Converting reactor projects to releases...");
        // Extract necessary session data.
        final ProjectBuildingRequest projectBuildingRequest = session
                .getProjectBuildingRequest();
        final List<MavenProject> projects = session.getProjects();
        // Setup release objects.
        final ReleaseDescriptor releaseDescriptor = this
                .createReleaseDescriptor(session);
        final ReleaseEnvironment releaseEnvironment = this
                .getReleaseEnvironment(session);
        // Try all of this, to enable possible rollback.
        try {
            // Execute the release phases.
            try {
                executeReleasePhases(releaseDescriptor, releaseEnvironment,
                        projects);
            } catch (final ReleaseExecutionException | ReleaseFailureException e) {
                throw new MavenExecutionException(
                        "Could not execute release phases\n"
                                + ExceptionUtils.getFullStackTrace(e), e);
            }
            // Rebuild the list of projects again, so that the POM rewrites take
            // effect.
            final List<MavenProject> newProjects = new ArrayList<MavenProject>(
                    projects.size());
            File pomFile;
            MavenProject newProject;
            for (final MavenProject project : projects) {
                pomFile = project.getFile();
                try {
                    newProject = builder.build(pomFile, projectBuildingRequest)
                            .getProject();
                } catch (final ProjectBuildingException e) {
                    throw new MavenExecutionException(
                            "Could not create release Maven project\n"
                                    + ExceptionUtils.getFullStackTrace(e),
                            pomFile);
                }
                if (project.isExecutionRoot()) {
                    newProject.setExecutionRoot(true);
                }
                newProjects.add(newProject);
            }
            // Set the new list of projects.
            session.setProjects(newProjects);
        } catch (final Exception e) {
            logger.info("Rolling back release due to error...");
            rollbackReleaseWork(releaseDescriptor, releaseEnvironment, projects);
            throw e;
        }
    }

    /**
     * Creates a release descriptor for the given Maven session.
     * 
     * @param session
     *            not null.
     * @return not null.
     */
    // Derived from AbstractReleaseMojo.java in maven-release-plugin, see
    // THIRDPARTY file for further legal information.
    private ReleaseDescriptor createReleaseDescriptor(final MavenSession session) {
        final ReleaseDescriptor descriptor = new ReleaseDescriptor();
        descriptor.setInteractive(session.getSettings().isInteractiveMode());
        descriptor.setWorkingDirectory(session.getRequest().getBaseDirectory());
        return descriptor;
    }

    /**
     * Creates a release environment for the given Maven session.
     * 
     * @param session
     *            not null.
     * @return not null.
     */
    // Derived from AbstractReleaseMojo.java in maven-release-plugin, see
    // THIRDPARTY file for further legal information.
    private ReleaseEnvironment getReleaseEnvironment(final MavenSession session) {
        return new DefaultReleaseEnvironment()
                .setSettings(session.getSettings())
                .setJavaHome(
                        new File(session.getSystemProperties().getProperty(
                                "user.home")))
                .setMavenHome(
                        new File(session.getSystemProperties().getProperty(
                                "maven.home")))
                .setLocalRepositoryDirectory(
                        session.getRequest().getLocalRepositoryPath());
    }

    /**
     * Executes the release phases necessary to setup the reactor projects for
     * the upcoming release build.
     * 
     * @param config
     *            the release descriptor, not null.
     * @param env
     *            the release environment, not null.
     * @param reactorProjects
     *            the projects to be release, not null and not empty.
     */
    // Derived from DefaultReleaseManager.java in maven-release-manager, see
    // THIRDPARTY file for further legal information.
    private void executeReleasePhases(final ReleaseDescriptor config,
            final ReleaseEnvironment env,
            final List<MavenProject> reactorProjects)
            throws ReleaseExecutionException, ReleaseFailureException {
        String name;
        ReleasePhase phase;
        ReleaseResult result;
        for (int i = 0; i < releasePhases.size(); i++) {
            name = releasePhases.get(i);
            phase = availablePhases.get(name);
            if (phase == null) {
                throw new ReleaseExecutionException("Unable to find phase '"
                        + name + "' to execute");
            }
            result = phase.execute(config, env, reactorProjects);
            if (result.getResultCode() == ReleaseResult.ERROR) {
                throw new ReleaseFailureException("Release failed.");
            }
        }
    }

    /**
     * Rolls back a failed release. All exceptions are logged but not thrown.
     * 
     * @param config
     *            the release descriptor, not null.
     * @param env
     *            the release environment, not null.
     * @param reactorProjects
     *            the projects to be release, not null and not empty.
     */
    // Derived from DefaultReleaseManager.java in maven-release-manager, see
    // THIRDPARTY file for further legal information.
    private void rollbackReleaseWork(final ReleaseDescriptor config,
            final ReleaseEnvironment env,
            final List<MavenProject> reactorProjects) {
        String name;
        ReleasePhase phase;
        ReleaseResult result;
        for (int i = 0; i < rollbackPhases.size(); i++) {
            name = rollbackPhases.get(i);
            phase = availablePhases.get(name);
            if (phase == null) {
                logger.error("Unable to find phase '" + name + "' to execute");
                break;
            }
            result = null;
            try {
                result = phase.execute(config, env, reactorProjects);
            } catch (final ReleaseExecutionException | ReleaseFailureException e) {
                logger.error(ExceptionUtils.getFullStackTrace(e));
                logger.error("Exception during rollback. Check project filesystem for POM backups and other resources that must be rolled back manually.");
            }
            if (result != null && result.getResultCode() == ReleaseResult.ERROR) {
                logger.error("Rollback failed. Check project filesystem for POM backups and other resources that must be rolled back manually.");
                break;
            }
        }
    }

}
