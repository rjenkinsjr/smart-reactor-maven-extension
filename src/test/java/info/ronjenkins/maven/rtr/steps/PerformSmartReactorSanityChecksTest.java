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

import info.ronjenkins.maven.rtr.RTRConfig;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;

import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

import util.TestLogger;
import util.TestUtils;

public final class PerformSmartReactorSanityChecksTest {

  @Injectable
  MavenSession session;
  @Mocked
  MavenProject root;
  @Mocked
  RTRConfig config;

  @Test
  public void singleProjectNonPomReactorAlwaysWorks() {
    final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        PerformSmartReactorSanityChecksTest.this.session.getProjects().size();
        this.result = 1;
        PerformSmartReactorSanityChecksTest.this.session.getTopLevelProject();
        this.result = PerformSmartReactorSanityChecksTest.this.root;
        PerformSmartReactorSanityChecksTest.this.root.getArtifact().getType();
        this.result = "jar";

      }
    };
    try {
      step.execute(this.session, null);
    } catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }

  @Test
  public void singleProjectPomReactorFailsIfNotAllowed() {
    final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        PerformSmartReactorSanityChecksTest.this.session.getProjects().size();
        this.result = 1;
        PerformSmartReactorSanityChecksTest.this.session.getTopLevelProject();
        this.result = PerformSmartReactorSanityChecksTest.this.root;
        PerformSmartReactorSanityChecksTest.this.root.getArtifact().getType();
        this.result = "pom";
        RTRConfig.isSinglePomReactorAllowed(
            PerformSmartReactorSanityChecksTest.this.session,
            PerformSmartReactorSanityChecksTest.this.root);
        this.result = false;
      }
    };
    try {
      step.execute(this.session, null);
    } catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorSanityCheckException);
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }

  @Test
  public void singleProjectPomReactorWorksIfAllowed() {
    final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        PerformSmartReactorSanityChecksTest.this.session.getProjects().size();
        this.result = 1;
        PerformSmartReactorSanityChecksTest.this.session.getTopLevelProject();
        this.result = PerformSmartReactorSanityChecksTest.this.root;
        PerformSmartReactorSanityChecksTest.this.root.getArtifact().getType();
        this.result = "pom";
        RTRConfig.isSinglePomReactorAllowed(
            PerformSmartReactorSanityChecksTest.this.session,
            PerformSmartReactorSanityChecksTest.this.root);
        this.result = true;
      }
    };
    try {
      step.execute(this.session, null);
    } catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }

  @Test
  public void twoProjectReactorAlwaysWorks() {
    final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
    final TestLogger logger = TestUtils.addLogger(step);
    final List<MavenProject> projects = new MockUp<List<MavenProject>>() {
      /*
       * TODO I tried session.getProjects().size(); result = 2; in the
       * expectations block but it kept returning 1. Investigate further and
       * file a bug with JMockit if necessary.
       */
      @Mock
      int size() {
        return 2;
      }
    }.getMockInstance();
    new Expectations() {
      {
        PerformSmartReactorSanityChecksTest.this.session.getProjects();
        this.result = projects;
      }
    };
    try {
      step.execute(this.session, null);
    } catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }

}
