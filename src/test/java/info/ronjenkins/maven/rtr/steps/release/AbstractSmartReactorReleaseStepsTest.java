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

import static org.junit.Assert.*;
import static util.TestUtils.*;
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
import org.junit.Test;

import util.TestLogger;

public final class AbstractSmartReactorReleaseStepsTest {

    @Tested
    @Injectable
    AbstractSmartReactorReleaseStep step;
    @Injectable
    MavenSession session;
    @Mocked
    RTR rtr;
    @Mocked
    ReleaseDescriptor releaseDescriptor;
    @Mocked
    ReleaseEnvironment releaseEnvironment;

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
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		null, null, null);
	new Expectations() {
	    {
		rtr.isRelease();
		result = false;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    fail();
	}
	assertTrue(logger.getErrorLog().isEmpty());
    }

    @Test
    public void successfulExecution(
	    @Injectable final Map<String, ReleasePhase> availablePhases) {
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		availablePhases, this.releaseDescriptor,
		this.releaseEnvironment);
	new Expectations() {
	    {
		rtr.isRelease();
		result = true;
		step.getReleasePhases();
		result = "phase1";
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    fail();
	}
	assertTrue(logger.getErrorLog().isEmpty());
    }

    @Test
    public void nullReleasePhaseCausesException(
	    @Injectable final Map<String, ReleasePhase> availablePhases) {
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		availablePhases, this.releaseDescriptor,
		this.releaseEnvironment);
	new Expectations() {
	    {
		rtr.isRelease();
		result = true;
		step.getReleasePhases();
		result = "phase1";
		step.getRollbackPhases();
		result = "phase2";
		availablePhases.get("phase1");
		result = null;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    assertTrue(e instanceof SmartReactorReleaseException);
	    assertEquals(0, e.getSuppressed().length);
	    assertEquals(IllegalStateException.class, e.getCause().getClass());
	}
	assertFalse(logger.getErrorLog().isEmpty());
    }

    @Test
    public void releasePhaseExceptionCausesExceptionWithProperCause(
	    @Injectable final Map<String, ReleasePhase> availablePhases,
	    @Injectable final ReleasePhase phase1) {
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		availablePhases, this.releaseDescriptor,
		this.releaseEnvironment);
	try {
	    new Expectations() {
		{
		    rtr.isRelease();
		    result = true;
		    step.getReleasePhases();
		    result = "phase1";
		    availablePhases.get("phase1");
		    result = phase1;
		    @SuppressWarnings({ "unused", "unchecked" })
		    ReleaseResult execute = phase1.execute(
			    (ReleaseDescriptor) any, (ReleaseEnvironment) any,
			    (List<MavenProject>) any);
		    result = new ReleaseExecutionException(
			    "test execution exception");
		}
	    };
	} catch (final ReleaseExecutionException | ReleaseFailureException notPossibleDuringTesting) {
	    notPossibleDuringTesting.printStackTrace();
	    fail();
	}
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    assertTrue(e instanceof SmartReactorReleaseException);
	    assertEquals(0, e.getSuppressed().length);
	    assertEquals(ReleaseExecutionException.class, e.getCause()
		    .getClass());
	}
	assertFalse(logger.getErrorLog().isEmpty());
    }

    @Test
    public void releasePhaseErrorResultCausesExceptionWithProperCause(
	    @Injectable final Map<String, ReleasePhase> availablePhases,
	    @Injectable final ReleasePhase phase1,
	    @Injectable final ReleaseResult result1) {
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		availablePhases, this.releaseDescriptor,
		this.releaseEnvironment);
	try {
	    new Expectations() {
		{
		    rtr.isRelease();
		    result = true;
		    step.getReleasePhases();
		    result = "phase1";
		    availablePhases.get("phase1");
		    result = phase1;
		    @SuppressWarnings({ "unused", "unchecked" })
		    ReleaseResult execute = phase1.execute(
			    (ReleaseDescriptor) any, (ReleaseEnvironment) any,
			    (List<MavenProject>) any);
		    result = result1;
		    result1.getResultCode();
		    result = ReleaseResult.ERROR;
		}
	    };
	} catch (final ReleaseExecutionException | ReleaseFailureException notPossibleDuringTesting) {
	    notPossibleDuringTesting.printStackTrace();
	    fail();
	}
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    assertTrue(e instanceof SmartReactorReleaseException);
	    assertEquals(0, e.getSuppressed().length);
	    assertEquals(IllegalStateException.class, e.getCause().getClass());
	}
	assertFalse(logger.getErrorLog().isEmpty());
    }

    @Test
    public void rollbackFailureOfAnyKindCausesExceptionSuppression(
	    @Injectable final Map<String, ReleasePhase> availablePhases) {
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		availablePhases, this.releaseDescriptor,
		this.releaseEnvironment);
	new Expectations() {
	    {
		rtr.isRelease();
		result = true;
		step.getReleasePhases();
		result = "phase1";
		step.getRollbackPhases();
		result = "phase2";
		availablePhases.get("phase1");
		result = null;
		availablePhases.get("phase2");
		result = null;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    assertTrue(e instanceof SmartReactorReleaseException);
	    assertEquals(1, e.getSuppressed().length);
	}
	assertFalse(logger.getErrorLog().isEmpty());
    }

}
