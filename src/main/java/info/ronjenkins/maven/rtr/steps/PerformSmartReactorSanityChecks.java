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
package info.ronjenkins.maven.rtr.steps;

import info.ronjenkins.maven.rtr.RTRComponents;
import info.ronjenkins.maven.rtr.RTRConfig;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Confirms that the Smart Reactor meets basic post-construction requirements.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "sanity-checks")
public class PerformSmartReactorSanityChecks extends AbstractSmartReactorStep {

    @Override
    public void execute(final MavenSession session,
      final RTRComponents components) throws MavenExecutionException {
  // Check for a single POM-only reactor, assuming this is prohibited.
  if (session.getProjects().size() == 1) {
      final MavenProject executionRoot = session.getTopLevelProject();
      if (executionRoot.getArtifact().getType().equals("pom")) {
    if (!RTRConfig
      .isSinglePomReactorAllowed(session, executionRoot)) {
        this.logger.error("");
        throw new SmartReactorSanityCheckException(
          "Reactor contains a single POM-packaging project, which is not allowed. If this is intended, set property \""
            + RTRConfig.PROP_SINGLE_POM_REACTOR_ALLOWED
            + "\" to true.");
    }
      }
  }
    }

}
