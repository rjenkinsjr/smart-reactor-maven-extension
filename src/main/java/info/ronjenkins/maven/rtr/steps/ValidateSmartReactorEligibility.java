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
import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Validates that this Maven session is eligible to be processed by the Smart
 * Reactor.
 *
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "validate-eligibility")
public class ValidateSmartReactorEligibility extends AbstractSmartReactorStep {

  @Override
  public void execute(final MavenSession session, final RTRComponents components)
      throws MavenExecutionException {
    // Ensure that the root is a SNAPSHOT.
    final MavenProject root = session.getTopLevelProject();
    if (!root.getArtifact().isSnapshot()) {
      this.logger.error("");
      this.logger.error("Top-level project " + root + " is not a SNAPSHOT.");
      this.logger.error("");
      throw new SmartReactorSanityCheckException(
          "Reactor is ineligible to become a Smart Reactor.");
    }
    // Ensure that the ancestors of every SNAPSHOT are also SNAPSHOTs.
    final ProjectDependencyGraph pdg = session.getProjectDependencyGraph();
    final List<MavenProject> badProjects = new ArrayList<>();
    for (final MavenProject project : session.getProjects()) {
      if (project.getArtifact().isSnapshot()) {
        for (final MavenProject ancestor : pdg.getUpstreamProjects(project,
            true)) {
          if (!ancestor.getArtifact().isSnapshot()) {
            badProjects.add(ancestor);
          }
        }
      }
    }
    // Fail if necessary.
    if (!badProjects.isEmpty()) {
      this.logger.error("");
      this.logger
          .error("The following release projects in the reactor have SNAPSHOT dependencies in the reactor, which is not allowed:");
      for (final MavenProject badProject : badProjects) {
        this.logger.error("  " + badProject.getArtifact().toString() + " @ "
            + badProject.getFile().getAbsolutePath());
      }
      this.logger.error("");
      throw new SmartReactorSanityCheckException(
          "Reactor is ineligible to become a Smart Reactor.");
    }
  }

}
