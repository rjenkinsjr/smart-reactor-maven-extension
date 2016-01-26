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
import info.ronjenkins.maven.rtr.reactor.ReactorDependencyGraph;

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

    @Requirement
    private Logger logger;

    @Requirement
    private ProjectBuilder builder;

    private List<String> releasePhases;
    private List<String> rollbackPhases;
    private List<String> postReleasePhases;

    @Requirement(role = ReleasePhase.class)
    private Map<String, ReleasePhase> availablePhases;

    private boolean release;
    private ReleaseDescriptor releaseDescriptor;
    private ReleaseEnvironment releaseEnvironment;

    /**
     * RTR entry point.
     * 
     * @param session
     *            the current Maven session, never null.
     */
    @Override
    public void afterProjectsRead(final MavenSession session)
            throws MavenExecutionException {
        // Don't do anything if the Smart Reactor is disabled.
        final MavenProject executionRoot = session.getTopLevelProject();
        if (RTRConfig.isDisabled(session, executionRoot)) {
            return;
        }
        this.logger.info("Assembling smart reactor...");
        // If the current reactor is not compatible with Smart Reactor
        // requirements, fail now.
        this.validateSmartReactorEligibility(session);
        // At this point, we know that all inter-dependent projects in the
        // reactor meet the Smart Reactor's requirements. We can now safely drop
        // all non-SNAPSHOT projects from the reactor. Do this now.
        this.buildSmartReactor(session);
        // Sanity checks.
        this.performSmartReactorSanityChecks(session);
        // Do a release, if it was requested.
        this.release = RTRConfig.isRelease(session, executionRoot);
        if (this.release) {
            this.releaseDescriptor = this.createReleaseDescriptor(session);
            this.releaseEnvironment = this.createReleaseEnvironment(session);
            this.transformProjectsIntoReleases(session);
            this.rebuildReleaseReactor(session);
        }
        // Recalculate the reactor dependency graph.
        this.rebuildProjectDependencyGraph(session);
        // Done. Maven build will proceed from here, none the wiser. ;)
    }

    @Override
    public void afterSessionEnd(final MavenSession session)
            throws MavenExecutionException {
        // Don't do anything if the Smart Reactor is disabled.
        final MavenProject executionRoot = session.getTopLevelProject();
        if (RTRConfig.isDisabled(session, executionRoot)) {
            return;
        }
        // Finish or rollback the release, if it was performed.
        if (this.release) {
            final boolean successfulBuild = session.getResult().hasExceptions();
            if (successfulBuild) {
                this.logger.info("Cleaning up after release...");
                this.doPostRelease(session);
            } else {
                this.logger.info("Rolling back after failed release...");
                this.doRollback(session.getProjects());
            }
        }
    }

    private void validateSmartReactorEligibility(final MavenSession session)
            throws SmartReactorSanityCheckException {
        final ReactorDependencyGraph reactorGraph = new ReactorDependencyGraph(
                session);
        if (!reactorGraph.isSmartReactorCompatible()) {
            reactorGraph.error(logger);
            this.logger.error("");
            new SmartReactorSanityCheckException(
                    "One or more inter-dependency requirements were not met. See the above graph.");
        }
        reactorGraph.destroy();
    }

    private void buildSmartReactor(final MavenSession session) {
        // The reactor is a mutable list whose type is unknown, so rather than
        // reassign it via sessions.setProjects(), we will manipulate the live
        // list instead.
        final Iterator<MavenProject> iterator = session.getProjects()
                .iterator();
        MavenProject project;
        while (iterator.hasNext()) {
            project = iterator.next();
            if (!project.getArtifact().isSnapshot()) {
                iterator.remove();
            }
        }
    }

    private void performSmartReactorSanityChecks(final MavenSession session) {
        final List<MavenProject> reactor = session.getProjects();
        final MavenProject executionRoot = session.getTopLevelProject();
        // Check for an empty reactor.
        if (reactor.isEmpty()) {
            this.logger.error("");
            new SmartReactorSanityCheckException(
                    "Reactor is empty. Did you forget to bump one of your projects to a SNAPSHOT version?");
        }
        // Make sure the reactor contains the execution root.
        if (!reactor.contains(executionRoot)) {
            this.logger.error("");
            new SmartReactorSanityCheckException(
                    "Reactor does not contain execution root project. Did you forget to bump its version to a SNAPSHOT?");
        }
        // Check for a single POM-only reactor, assuming this is prohibited.
        final boolean isPomOnly = reactor.size() == 1
                && executionRoot.getArtifact().getType().equals("pom");
        if (isPomOnly) {
            final boolean singlePomOnlyReactorAllowed = RTRConfig
                    .isSinglePomReactorAllowed(session, executionRoot);
            if (!singlePomOnlyReactorAllowed) {
                this.logger.error("");
                new SmartReactorSanityCheckException(
                        "Reactor contains a single POM-packaging project, which is not allowed. If this is intended, set property \""
                                + RTRConfig.PROP_SINGLE_POM_REACTOR_ALLOWED
                                + "\" to true.");
            }
        }
    }

    // Derived from AbstractReleaseMojo.java in maven-release-plugin, see
    // THIRDPARTY file for further legal information.
    private ReleaseDescriptor createReleaseDescriptor(final MavenSession session) {
        final ReleaseDescriptor descriptor = new ReleaseDescriptor();
        descriptor.setInteractive(session.getSettings().isInteractiveMode());
        descriptor.setWorkingDirectory(session.getRequest().getBaseDirectory());
        return descriptor;
    }

    // Derived from AbstractReleaseMojo.java in maven-release-plugin, see
    // THIRDPARTY file for further legal information.
    private ReleaseEnvironment createReleaseEnvironment(
            final MavenSession session) {
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

    private void transformProjectsIntoReleases(final MavenSession session)
            throws MavenExecutionException {
        this.logger.info("Converting reactor projects to releases...");
        final List<MavenProject> reactor = session.getProjects();
        try {
            // Execute the release phases.
            try {
                executePhases(reactor, this.releasePhases);
            } catch (final ReleaseExecutionException | ReleaseFailureException e) {
                this.logger.error("");
                throw new MavenExecutionException(
                        "Could not execute release phases\n"
                                + ExceptionUtils.getFullStackTrace(e), e);
            }
        } catch (final Exception e) {
            // Rollback and rethrow.
            this.logger.info("Rolling back release due to error...");
            this.doRollback(reactor);
            throw e;
        }
    }

    private void doRollback(final List<MavenProject> reactor) {
        try {
            executePhases(reactor, this.rollbackPhases);
        } catch (final ReleaseExecutionException | ReleaseFailureException e2) {
            this.logger.error(ExceptionUtils.getFullStackTrace(e2));
            this.logger
                    .error("Rollback unsuccessful. Check project filesystem for POM backups and other resources that must be rolled back manually.");
        }
    }

    // Derived from DefaultReleaseManager.java in maven-release-manager, see
    // THIRDPARTY file for further legal information.
    private void executePhases(final List<MavenProject> reactor,
            final List<String> phases) throws ReleaseExecutionException,
            ReleaseFailureException {
        ReleasePhase phase;
        ReleaseResult result;
        for (final String name : phases) {
            phase = this.availablePhases.get(name);
            if (phase == null) {
                throw new ReleaseExecutionException("Unable to find phase '"
                        + name + "' to execute");
            }
            result = phase.execute(this.releaseDescriptor,
                    this.releaseEnvironment, reactor);
            if (result.getResultCode() == ReleaseResult.ERROR) {
                throw new ReleaseFailureException("Release failed.");
            }
        }
    }

    private void rebuildReleaseReactor(final MavenSession session)
            throws MavenExecutionException {
        final List<MavenProject> reactor = session.getProjects();
        final List<MavenProject> newReactor = new ArrayList<MavenProject>(
                reactor.size());
        File pomFile;
        MavenProject newProject;
        for (final MavenProject project : reactor) {
            pomFile = project.getFile();
            try {
                newProject = this.builder.build(pomFile,
                        session.getProjectBuildingRequest()).getProject();
            } catch (final ProjectBuildingException e) {
                this.logger.error("");
                throw new MavenExecutionException(
                        "Could not rebuild Maven project model\n"
                                + ExceptionUtils.getFullStackTrace(e), pomFile);
            }
            if (project.isExecutionRoot()) {
                newProject.setExecutionRoot(true);
            }
            newReactor.add(newProject);
        }
        // Set the new list of projects, but don't replace the actual list
        // object.
        session.getProjects().clear();
        session.getProjects().addAll(newReactor);
    }

    private void rebuildProjectDependencyGraph(final MavenSession session)
            throws MavenExecutionException {
        try {
            session.setProjectDependencyGraph(new DefaultProjectDependencyGraph(
                    session.getProjects()));
        } catch (final CycleDetectedException | DuplicateProjectException e) {
            this.logger.error("");
            throw new MavenExecutionException(
                    "Could not assemble new project dependency graph", e);
        }
    }

    private void doPostRelease(final MavenSession session)
            throws MavenExecutionException {
        // Execute the release phases.
        try {
            executePhases(session.getProjects(), this.postReleasePhases);
        } catch (final ReleaseExecutionException | ReleaseFailureException e) {
            this.logger.error("");
            throw new MavenExecutionException(
                    "Could not execute post-release phases\n"
                            + ExceptionUtils.getFullStackTrace(e), e);
        }
    }

}
