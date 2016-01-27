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
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.ExceptionUtils;

/**
 * Cleans up/rolls back a release build.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "post-release")
public class DoPostRelease extends AbstractSmartReactorReleaseStep {

    private List<String> postReleasePhases;

    @Override
    public void doReleaseStep(final MavenSession session,
            final RTRComponents components) throws MavenExecutionException {
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
