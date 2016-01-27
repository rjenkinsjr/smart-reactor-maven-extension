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
import org.codehaus.plexus.util.ExceptionUtils;

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

    protected List<String> rollbackPhases;

    @Requirement(role = ReleasePhase.class)
    protected Map<String, ReleasePhase> availablePhases;

    @Requirement(hint = "rtr-rd")
    protected ReleaseDescriptor releaseDescriptor;

    @Requirement(hint = "rtr-re")
    protected ReleaseEnvironment releaseEnvironment;

    @Override
    public void execute(final MavenSession session,
            final RTRComponents components) throws MavenExecutionException {
        if (this.rtr.isRelease()) {
            this.releaseEnvironment.setSettings(session.getSettings());
            this.doReleaseStep(session, components);
        }
    }

    /**
     * Executes this step.
     * 
     * @param session
     *            the session to which this step applies. Not null.
     * @param components
     *            that this step may need. May be null.
     * @throws MavenExecutionException
     *             if any unrecoverable error occurs.
     */
    protected abstract void doReleaseStep(final MavenSession session,
            final RTRComponents components) throws MavenExecutionException;

    protected final void doRollback(final List<MavenProject> reactor) {
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
    protected final void executePhases(final List<MavenProject> reactor,
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

}
