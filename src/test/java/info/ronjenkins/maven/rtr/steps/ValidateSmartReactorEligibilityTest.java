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

import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;

import java.util.Arrays;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

import util.TestLogger;
import util.TestUtils;

public final class ValidateSmartReactorEligibilityTest {
  @Injectable
  MavenSession session;
  @Mocked
  MavenProject root;

  @Test
  public void parentSnapshotAndChildReleaseIsEligible(
      @Injectable final MavenProject child) {
    final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        ValidateSmartReactorEligibilityTest.this.session.getTopLevelProject();
        this.result = ValidateSmartReactorEligibilityTest.this.root;
        ValidateSmartReactorEligibilityTest.this.session.getProjects();
        this.result = Arrays.asList(
            ValidateSmartReactorEligibilityTest.this.root, child);
        ValidateSmartReactorEligibilityTest.this.root.getArtifact()
        .isSnapshot();
        this.result = true;
        child.getArtifact().isSnapshot();
        this.result = false;
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail(ExceptionUtils.getFullStackTrace(e));
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }

  @Test
  public void parentSnapshotAndChildSnapshotIsEligible(
      @Injectable final ProjectDependencyGraph pdg,
      @Injectable final MavenProject child) {
    final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        ValidateSmartReactorEligibilityTest.this.session.getTopLevelProject();
        this.result = ValidateSmartReactorEligibilityTest.this.root;
        ValidateSmartReactorEligibilityTest.this.session.getProjects();
        this.result = Arrays.asList(
            ValidateSmartReactorEligibilityTest.this.root, child);
        ValidateSmartReactorEligibilityTest.this.session
        .getProjectDependencyGraph();
        this.result = pdg;
        ValidateSmartReactorEligibilityTest.this.root.getArtifact()
        .isSnapshot();
        this.result = true;
        child.getArtifact().isSnapshot();
        this.result = true;
        pdg.getUpstreamProjects(child, true);
        this.result = ValidateSmartReactorEligibilityTest.this.root;
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail(ExceptionUtils.getFullStackTrace(e));
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }

  @Test
  public void singleReleaseIsNotEligible() {
    final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        ValidateSmartReactorEligibilityTest.this.session.getTopLevelProject();
        this.result = ValidateSmartReactorEligibilityTest.this.root;
        ValidateSmartReactorEligibilityTest.this.root.getArtifact()
        .isSnapshot();
        this.result = false;
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorSanityCheckException);
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }

  @Test
  public void singleSnapshotIsEligible() {
    final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        ValidateSmartReactorEligibilityTest.this.session.getTopLevelProject();
        this.result = ValidateSmartReactorEligibilityTest.this.root;
        ValidateSmartReactorEligibilityTest.this.root.getArtifact()
        .isSnapshot();
        this.result = true;
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail(ExceptionUtils.getFullStackTrace(e));
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }

  @Test
  public void threeGenerationSnapshotFamilyIsEligible(
      @Injectable final ProjectDependencyGraph pdg,
      @Injectable final MavenProject child,
      @Injectable final MavenProject grandchild) {
    final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        ValidateSmartReactorEligibilityTest.this.session.getTopLevelProject();
        this.result = ValidateSmartReactorEligibilityTest.this.root;
        ValidateSmartReactorEligibilityTest.this.session.getProjects();
        this.result = Arrays.asList(
            ValidateSmartReactorEligibilityTest.this.root, child, grandchild);
        ValidateSmartReactorEligibilityTest.this.session
        .getProjectDependencyGraph();
        this.result = pdg;
        ValidateSmartReactorEligibilityTest.this.root.getArtifact()
        .isSnapshot();
        this.result = true;
        child.getArtifact().isSnapshot();
        this.result = true;
        grandchild.getArtifact().isSnapshot();
        this.result = true;
        pdg.getUpstreamProjects(child, true);
        this.result = ValidateSmartReactorEligibilityTest.this.root;
        pdg.getUpstreamProjects(grandchild, true);
        this.result = Arrays.asList(child,
            ValidateSmartReactorEligibilityTest.this.root);
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail(ExceptionUtils.getFullStackTrace(e));
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }

  @Test
  public void threeGenerationSnapshotFamilyWithReleaseChildIsNotEligible(
      @Injectable final ProjectDependencyGraph pdg,
      @Injectable final MavenProject child,
      @Injectable final MavenProject grandchild) {
    final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
    final TestLogger logger = TestUtils.addLogger(step);
    new Expectations() {
      {
        ValidateSmartReactorEligibilityTest.this.session.getTopLevelProject();
        this.result = ValidateSmartReactorEligibilityTest.this.root;
        ValidateSmartReactorEligibilityTest.this.session.getProjects();
        this.result = Arrays.asList(
            ValidateSmartReactorEligibilityTest.this.root, child, grandchild);
        ValidateSmartReactorEligibilityTest.this.session
        .getProjectDependencyGraph();
        this.result = pdg;
        ValidateSmartReactorEligibilityTest.this.root.getArtifact()
        .isSnapshot();
        this.result = true;
        child.getArtifact().isSnapshot();
        this.result = false;
        grandchild.getArtifact().isSnapshot();
        this.result = true;
        pdg.getUpstreamProjects(grandchild, true);
        this.result = Arrays.asList(child,
            ValidateSmartReactorEligibilityTest.this.root);
      }
    };
    try {
      step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorSanityCheckException);
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }
}
