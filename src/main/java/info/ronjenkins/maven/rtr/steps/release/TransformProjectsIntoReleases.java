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

import info.ronjenkins.maven.rtr.RTRComponents;
import info.ronjenkins.maven.rtr.steps.SmartReactorStep;

import java.util.List;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.ExceptionUtils;

/**
 * Transforms all projects in the Smart Reactor from SNAPSHOTs to non-SNAPSHOTs.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "transform-poms")
public class TransformProjectsIntoReleases extends
        AbstractSmartReactorReleaseStep {

    private List<String> releasePhases;

    @Override
    public void doReleaseStep(final MavenSession session,
            final RTRComponents components) throws MavenExecutionException {
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

}
