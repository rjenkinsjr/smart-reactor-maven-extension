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

import static org.junit.Assert.*;
import static util.TestUtils.*;

import java.util.Arrays;

import mockit.Expectations;
import mockit.Injectable;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import util.TestLogger;

public final class RebuildProjectDependencyGraphTest {

    @Injectable
    MavenSession session;
    @Injectable
    MavenProject root;

    @Test
    public void successfulExecution() {
  final RebuildProjectDependencyGraph step = new RebuildProjectDependencyGraph();
  final TestLogger logger = addLogger(step);
  new Expectations() {
      {
    session.getProjects();
    result = root;
    root.getGroupId();
    result = "test";
    root.getArtifactId();
    result = "root";
    root.getVersion();
    result = "1";
    root.getModel().getParent();
    result = null;
      }
  };
  try {
      step.execute(session, null);
  } catch (final MavenExecutionException e) {
      fail();
  } catch (final Exception e) {
      e.printStackTrace();
      fail();
  }
  assertTrue(logger.getErrorLog().isEmpty());
    }

    @Test
    public void exceptionsGetTranslated() {
  final RebuildProjectDependencyGraph step = new RebuildProjectDependencyGraph();
  final TestLogger logger = addLogger(step);
  new Expectations() {
      {
    session.getProjects();
    result = Arrays.asList(root, root);
    root.getGroupId();
    result = "test";
    root.getArtifactId();
    result = "root";
    root.getVersion();
    result = "1";
      }
  };
  try {
      step.execute(session, null);
  } catch (final MavenExecutionException e) {
      assertTrue(e instanceof MavenExecutionException);
      assertNotNull(e.getCause());
  }
  assertFalse(logger.getErrorLog().isEmpty());
    }

}
