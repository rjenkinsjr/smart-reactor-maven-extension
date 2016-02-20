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
import info.ronjenkins.maven.rtr.exceptions.SmartReactorReleaseException;

import java.util.List;
import java.util.Map;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Tested;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.ReleasePhase;
import org.junit.Assert;
import org.junit.Test;

import util.TestLogger;
import util.TestUtils;

public final class AbstractSmartReactorReleaseStepsTest {
  @Tested
  @Injectable
  AbstractSmartReactorReleaseStep step;
  @Injectable
  MavenSession                    session;
  @Mocked
  RTR                             rtr;
  @Mocked
  ReleaseDescriptor               releaseDescriptor;
  @Mocked
  ReleaseEnvironment              releaseEnvironment;
  
  @Test
  public void coverBasicImplementations() {
    final TransformProjectsIntoReleases tpir = new TransformProjectsIntoReleases();
    tpir.getAnnouncement();
    tpir.getReleasePhases();
    tpir.getRollbackPhases();
    final DoPostReleaseSuccess dprs = new DoPostReleaseSuccess();
    dprs.getAnnouncement();
    dprs.getReleasePhases();
    dprs.getRollbackPhases();
    final DoPostReleaseFailure dprf = new DoPostReleaseFailure();
    dprf.getAnnouncement();
    dprf.getReleasePhases();
    dprf.getRollbackPhases();
  }
  
  @Test
  public void disabledReleaseMeansNoop() {
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(
        this.step, this.rtr, null, null, null);
    new Expectations() {
      {
        AbstractSmartReactorReleaseStepsTest.this.rtr.isRelease();
        this.result = false;
      }
    };
    try {
      this.step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }
  
  @Test
  public void nullReleasePhaseCausesException(
      @Injectable final Map<String, ReleasePhase> availablePhases) {
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(
        this.step, this.rtr, availablePhases, this.releaseDescriptor,
        this.releaseEnvironment);
    new Expectations() {
      {
        AbstractSmartReactorReleaseStepsTest.this.rtr.isRelease();
        this.result = true;
        AbstractSmartReactorReleaseStepsTest.this.step.getReleasePhases();
        this.result = "phase1";
        AbstractSmartReactorReleaseStepsTest.this.step.getRollbackPhases();
        this.result = "phase2";
        availablePhases.get("phase1");
        this.result = null;
      }
    };
    try {
      this.step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorReleaseException);
      Assert.assertEquals(0, e.getSuppressed().length);
      Assert.assertEquals(IllegalStateException.class, e.getCause().getClass());
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void releasePhaseErrorResultCausesExceptionWithProperCause(
      @Injectable final Map<String, ReleasePhase> availablePhases,
      @Injectable final ReleasePhase phase1,
      @Injectable final ReleaseResult result1) {
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(
        this.step, this.rtr, availablePhases, this.releaseDescriptor,
        this.releaseEnvironment);
    try {
      new Expectations() {
        {
          AbstractSmartReactorReleaseStepsTest.this.rtr.isRelease();
          this.result = true;
          AbstractSmartReactorReleaseStepsTest.this.step.getReleasePhases();
          this.result = "phase1";
          availablePhases.get("phase1");
          this.result = phase1;
          phase1.execute((ReleaseDescriptor) this.any,
              (ReleaseEnvironment) this.any, (List<MavenProject>) this.any);
          this.result = result1;
          result1.getResultCode();
          this.result = ReleaseResult.ERROR;
        }
      };
    }
    catch (final ReleaseExecutionException | ReleaseFailureException notPossibleDuringTesting) {
      notPossibleDuringTesting.printStackTrace();
      Assert.fail();
    }
    try {
      this.step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorReleaseException);
      Assert.assertEquals(0, e.getSuppressed().length);
      Assert.assertEquals(IllegalStateException.class, e.getCause().getClass());
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void releasePhaseExceptionCausesExceptionWithProperCause(
      @Injectable final Map<String, ReleasePhase> availablePhases,
      @Injectable final ReleasePhase phase1) {
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(
        this.step, this.rtr, availablePhases, this.releaseDescriptor,
        this.releaseEnvironment);
    try {
      new Expectations() {
        {
          AbstractSmartReactorReleaseStepsTest.this.rtr.isRelease();
          this.result = true;
          AbstractSmartReactorReleaseStepsTest.this.step.getReleasePhases();
          this.result = "phase1";
          availablePhases.get("phase1");
          this.result = phase1;
          phase1.execute((ReleaseDescriptor) this.any,
              (ReleaseEnvironment) this.any, (List<MavenProject>) this.any);
          this.result = new ReleaseExecutionException(
              "test execution exception");
        }
      };
    }
    catch (final ReleaseExecutionException | ReleaseFailureException notPossibleDuringTesting) {
      notPossibleDuringTesting.printStackTrace();
      Assert.fail();
    }
    try {
      this.step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorReleaseException);
      Assert.assertEquals(0, e.getSuppressed().length);
      Assert.assertEquals(ReleaseExecutionException.class, e.getCause()
          .getClass());
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }
  
  @Test
  public void rollbackFailureOfAnyKindCausesExceptionSuppression(
      @Injectable final Map<String, ReleasePhase> availablePhases) {
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(
        this.step, this.rtr, availablePhases, this.releaseDescriptor,
        this.releaseEnvironment);
    new Expectations() {
      {
        AbstractSmartReactorReleaseStepsTest.this.rtr.isRelease();
        this.result = true;
        AbstractSmartReactorReleaseStepsTest.this.step.getReleasePhases();
        this.result = "phase1";
        AbstractSmartReactorReleaseStepsTest.this.step.getRollbackPhases();
        this.result = "phase2";
        availablePhases.get("phase1");
        this.result = null;
        availablePhases.get("phase2");
        this.result = null;
      }
    };
    try {
      this.step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorReleaseException);
      Assert.assertEquals(1, e.getSuppressed().length);
    }
    Assert.assertFalse(logger.getErrorLog().isEmpty());
  }
  
  @Test
  public void successfulExecution(
      @Injectable final Map<String, ReleasePhase> availablePhases) {
    final TestLogger logger = TestUtils.addLoggerAndReleaseDependencies(
        this.step, this.rtr, availablePhases, this.releaseDescriptor,
        this.releaseEnvironment);
    new Expectations() {
      {
        AbstractSmartReactorReleaseStepsTest.this.rtr.isRelease();
        this.result = true;
        AbstractSmartReactorReleaseStepsTest.this.step.getReleasePhases();
        this.result = "phase1";
      }
    };
    try {
      this.step.execute(this.session, null);
    }
    catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getErrorLog().isEmpty());
  }
}
