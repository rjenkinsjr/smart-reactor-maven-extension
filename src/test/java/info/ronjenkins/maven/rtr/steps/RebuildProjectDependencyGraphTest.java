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

import java.util.Arrays;

import mockit.Expectations;
import mockit.Injectable;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

import util.TestLogger;
import util.TestUtils;

public final class RebuildProjectDependencyGraphTest {
  @Injectable
  MavenSession session;
  @Injectable
  MavenProject root;
  
  @Test
  public void exceptionsGetTranslated() {
    final RebuildProjectDependencyGraph step = new RebuildProjectDependencyGraph();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        RebuildProjectDependencyGraphTest.this.session.getProjects();
        this.result = Arrays.asList(
            RebuildProjectDependencyGraphTest.this.root,
            RebuildProjectDependencyGraphTest.this.root);
        RebuildProjectDependencyGraphTest.this.root.getGroupId();
        this.result = "test";
        RebuildProjectDependencyGraphTest.this.root.getArtifactId();
        this.result = "root";
        RebuildProjectDependencyGraphTest.this.root.getVersion();
        this.result = "1";
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof MavenExecutionException);
      Assert.assertNotNull(e.getCause());
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }
  
  @Test
  public void successfulExecution() {
    final RebuildProjectDependencyGraph step = new RebuildProjectDependencyGraph();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        RebuildProjectDependencyGraphTest.this.session.getProjects();
        this.result = RebuildProjectDependencyGraphTest.this.root;
        RebuildProjectDependencyGraphTest.this.root.getGroupId();
        this.result = "test";
        RebuildProjectDependencyGraphTest.this.root.getArtifactId();
        this.result = "root";
        RebuildProjectDependencyGraphTest.this.root.getVersion();
        this.result = "1";
        RebuildProjectDependencyGraphTest.this.root.getModel().getParent();
        this.result = null;
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail();
    }
    catch (final Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }
}
