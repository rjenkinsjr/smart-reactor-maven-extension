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
package info.ronjenkins.maven.rtr.steps.release;

import info.ronjenkins.maven.rtr.RTR;
import info.ronjenkins.maven.rtr.RTRComponents;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorReleaseException;
import info.ronjenkins.maven.rtr.steps.AbstractSmartReactorStep;

import java.util.List;
import java.util.Map;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.ReleasePhase;
import org.codehaus.plexus.component.annotations.Requirement;

/**
 * Base implementation of a Smart Reactor release step. No-op unless the Smart
 * Reactor's release property is true at runtime.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public abstract class AbstractSmartReactorReleaseStep extends
	AbstractSmartReactorStep {

    @Requirement(role = AbstractMavenLifecycleParticipant.class, hint = "rtr")
    protected RTR rtr;

    @Requirement(role = ReleasePhase.class)
    protected Map<String, ReleasePhase> availablePhases;

    @Requirement(hint = "rtr-rd")
    protected ReleaseDescriptor releaseDescriptor;

    @Requirement(hint = "rtr-re")
    protected ReleaseEnvironment releaseEnvironment;

    @Override
    public final void execute(final MavenSession session,
	    final RTRComponents components) throws MavenExecutionException {
	if (this.rtr.isRelease()) {
	    this.logger.info(this.getAnnouncement());
	    this.configureReleaseDescriptor(session, components);
	    this.releaseExecute(session, components);
	}
    }

    /**
     * Returns the announcement that is logged when this release step begins
     * execution.
     * 
     * @return null or empty will prevent the corresponding log entry from
     *         occurring.
     */
    protected abstract String getAnnouncement();

    /**
     * Subclasses can override this method to configure the release descriptor
     * injected by Plexus. The default implementation does nothing.
     * 
     * @param session
     *            the session to which this step applies. Not null.
     * @param components
     *            that this step may need. May be null.
     */
    protected void configureReleaseDescriptor(final MavenSession session,
	    final RTRComponents components) {
    }

    /**
     * Returns the list of phases that should be executed by this release step.
     * 
     * @return never null or empty.
     * @throws UnsupportedOperationException
     *             if this step does not execute any release phases.
     */
    protected abstract List<String> getReleasePhases();

    /**
     * Returns the list of phases that should be executed by this release step
     * when rollback is required.
     * 
     * @return never null or empty.
     * @throws UnsupportedOperationException
     *             if this step does not execute any release phases.
     */
    protected abstract List<String> getRollbackPhases();

    /**
     * Step logic that is executed if a release was requested.
     * 
     * @param session
     *            the session to which this step applies. Not null.
     * @param components
     *            that this step may need. May be null.
     * @throws MavenExecutionException
     *             if any unrecoverable error occurs.
     */
    protected void releaseExecute(final MavenSession session,
	    final RTRComponents components) throws MavenExecutionException {
	this.releaseEnvironment.setSettings(session.getSettings());
	final List<MavenProject> reactor = session.getProjects();
	// Execute the release steps.
	try {
	    this.runPhases(reactor, this.getReleasePhases());
	} catch (final MavenExecutionException | RuntimeException e) {
	    // Rollback if ANY exception occurred, then rethrow.
	    this.logger.error("Rolling back release due to error...");
	    try {
		this.runPhases(reactor, this.getRollbackPhases());
	    } catch (final MavenExecutionException | RuntimeException e2) {
		// Suppress this exception.
		e.addSuppressed(e2);
		this.logger
			.error("Rollback unsuccessful. Check project filesystem for POM backups and other resources that must be rolled back manually.");
	    }
	    throw e;
	}
    }

    // Derived from DefaultReleaseManager.java in maven-release-manager, see
    // THIRDPARTY file for further legal information.
    private final void runPhases(final List<MavenProject> reactor,
	    final List<String> phases) throws MavenExecutionException {
	ReleasePhase phase;
	ReleaseResult result;
	for (final String name : phases) {
	    phase = this.availablePhases.get(name);
	    if (phase == null) {
		throw new SmartReactorReleaseException("Unable to find phase '"
			+ name + "' to execute");
	    }
	    try {
		result = phase.execute(this.releaseDescriptor,
			this.releaseEnvironment, reactor);
	    } catch (final ReleaseExecutionException | ReleaseFailureException e) {
		throw new SmartReactorReleaseException(e);
	    }
	    if (result.getResultCode() == ReleaseResult.ERROR) {
		throw new SmartReactorReleaseException(result.getOutput());
	    }
	}
    }

}
