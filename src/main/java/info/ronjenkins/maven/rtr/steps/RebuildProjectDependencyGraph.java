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

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.graph.DefaultProjectDependencyGraph;
import org.apache.maven.project.DuplicateProjectException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.util.dag.CycleDetectedException;

/**
 * Rebuilds the dependency graph for the session after all project changes have
 * taken effect.
 *
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "rebuild-graph")
public class RebuildProjectDependencyGraph extends AbstractSmartReactorStep {

  @Override
  public void execute(final MavenSession session, final RTRComponents components)
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

}
