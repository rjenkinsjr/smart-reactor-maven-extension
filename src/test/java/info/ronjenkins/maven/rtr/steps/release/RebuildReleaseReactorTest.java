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
import info.ronjenkins.maven.rtr.exceptions.SmartReactorReleaseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.junit.Assert;
import org.junit.Test;

import util.TestLogger;
import util.TestUtils;

public final class RebuildReleaseReactorTest {
  @Injectable
  MavenSession   session;
  @Injectable
  RTRComponents  components;
  @Mocked
  ProjectBuilder projectBuilder;
  @Mocked
  RTR            rtr;
  
  @Test
  public void assertUOEs() {
    final RebuildReleaseReactor step = new RebuildReleaseReactor();
    try {
      step.getReleasePhases();
      Assert.fail();
    }
    catch (final UnsupportedOperationException expected) {
      // expected
    }
    catch (final Exception notExpected) {
      Assert.fail();
    }
    try {
      step.getRollbackPhases();
      Assert.fail();
    }
    catch (final UnsupportedOperationException expected) {
      // expected
    }
    catch (final Exception notExpected) {
      Assert.fail();
    }
  }
  
  @Test
  public void disabledReleaseMeansNoop() {
    final RebuildReleaseReactor step = new RebuildReleaseReactor();
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(step,
        this.rtr, null, null, null);
    new Expectations() {
      {
        RebuildReleaseReactorTest.this.rtr.isRelease();
        this.result = false;
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }
  
  @Test
  public void exceptionsArePropagated(@Injectable final MavenProject root) {
    final RebuildReleaseReactor step = new RebuildReleaseReactor();
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(step,
        this.rtr, null, null, null);
    final ProjectBuildingException pbe = new ProjectBuildingException("id",
        "error", root.getFile());
    try {
      new Expectations() {
        {
          RebuildReleaseReactorTest.this.rtr.isRelease();
          this.result = true;
          RebuildReleaseReactorTest.this.session.getProjects();
          this.result = root;
          RebuildReleaseReactorTest.this.components.getProjectBuilder();
          this.result = RebuildReleaseReactorTest.this.projectBuilder;
          RebuildReleaseReactorTest.this.projectBuilder.build((File) this.any,
              (ProjectBuildingRequest) this.any);
          this.result = pbe;
        }
      };
    }
    catch (final ProjectBuildingException notPossibleDuringTesting) {
      notPossibleDuringTesting.printStackTrace();
      Assert.fail();
    }
    try {
      step.execute(this.session, this.components);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorReleaseException);
      Assert.assertEquals(pbe, e.getCause());
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }
  
  @Test
  public void successfulExecution(@Injectable final MavenProject root,
      @Injectable final MavenProject child,
      @Injectable final ProjectBuildingResult rootResult,
      @Injectable final ProjectBuildingResult childResult) {
    final RebuildReleaseReactor step = new RebuildReleaseReactor();
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(step,
        this.rtr, null, null, null);
    final List<MavenProject> reactor = new ArrayList<MavenProject>();
    reactor.add(root);
    reactor.add(child);
    final File rootFile = new File("rootFile");
    final File childFile = new File("childFile");
    final MavenProject newRoot = new MavenProject();
    final MavenProject newChild = new MavenProject();
    try {
      new Expectations(rootFile, childFile, newRoot, newChild) {
        {
          RebuildReleaseReactorTest.this.rtr.isRelease();
          this.result = true;
          RebuildReleaseReactorTest.this.session.getProjects();
          this.result = reactor;
          root.getFile();
          this.result = rootFile;
          child.getFile();
          this.result = childFile;
          rootFile.equals(this.any);
          this.result = false;
          childFile.equals(this.any);
          this.result = false;
          RebuildReleaseReactorTest.this.components.getProjectBuilder();
          this.result = RebuildReleaseReactorTest.this.projectBuilder;
          RebuildReleaseReactorTest.this.projectBuilder.build(rootFile,
              RebuildReleaseReactorTest.this.session
                  .getProjectBuildingRequest());
          this.result = rootResult;
          RebuildReleaseReactorTest.this.projectBuilder.build(childFile,
              RebuildReleaseReactorTest.this.session
                  .getProjectBuildingRequest());
          this.result = childResult;
          rootResult.getProject();
          this.result = newRoot;
          childResult.getProject();
          this.result = newChild;
          root.isExecutionRoot();
          this.result = true;
          child.isExecutionRoot();
          this.result = false;
        }
      };
    }
    catch (final ProjectBuildingException notPossibleDuringTesting) {
      notPossibleDuringTesting.printStackTrace();
      Assert.fail();
    }
    try {
      step.execute(this.session, this.components);
    }
    catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
    // Do object identity comparisons.
    Assert.assertTrue(this.session.getProjects() == reactor);
    Assert.assertFalse(this.session.getProjects().get(0) == root);
    Assert.assertFalse(this.session.getProjects().get(1) == child);
    Assert.assertTrue(this.session.getProjects().get(0) == newRoot);
    Assert.assertTrue(this.session.getProjects().get(1) == newChild);
    // Do project checks.
    Assert.assertTrue(newRoot.isExecutionRoot());
    Assert.assertFalse(newChild.isExecutionRoot());
  }
}
