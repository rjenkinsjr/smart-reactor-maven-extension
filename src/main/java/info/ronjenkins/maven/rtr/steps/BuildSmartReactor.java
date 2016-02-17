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

import java.util.Iterator;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Reassembles the Maven session's reactor using Smart Reactor rules.
 *
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "build-smart-reactor")
public class BuildSmartReactor extends AbstractSmartReactorStep {

  @Override
  public void execute(final MavenSession session, final RTRComponents components) {
    // The reactor is a mutable list whose type is unknown, so rather than
    // reassign it via sessions.setProjects(), we will manipulate the live
    // list instead.
    final Iterator<MavenProject> iterator = session.getProjects().iterator();
    MavenProject project;
    while (iterator.hasNext()) {
      project = iterator.next();
      if (!project.getArtifact().isSnapshot()) {
        iterator.remove();
      }
    }
  }

}
