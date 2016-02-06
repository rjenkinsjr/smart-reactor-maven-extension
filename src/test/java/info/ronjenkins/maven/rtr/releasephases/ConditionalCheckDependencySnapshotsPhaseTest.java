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
package info.ronjenkins.maven.rtr.releasephases;

import static org.junit.Assert.*;
import static util.TestUtils.*;
import info.ronjenkins.maven.rtr.RTR;

import java.util.Arrays;
import java.util.List;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.CheckDependencySnapshotsPhase;
import org.junit.Test;

import util.TestLogger;

public final class ConditionalCheckDependencySnapshotsPhaseTest {

    @Injectable
    MavenProject root;
    @Injectable
    RTR rtr;

    @Test
    public void allowedMeansNoop() {
	final ConditionalCheckDependencySnapshotsPhase phase = new ConditionalCheckDependencySnapshotsPhase();
	final TestLogger logger = addLogger(phase);
	Deencapsulation.setField(phase, "rtr", this.rtr);
	new Expectations() {
	    {
		rtr.isExternalSnapshotsAllowed();
		result = true;
	    }
	};
	try {
	    final ReleaseResult result = phase.execute(
		    (ReleaseDescriptor) null, (ReleaseEnvironment) null,
		    (List<MavenProject>) null);
	    assertEquals(ReleaseResult.SUCCESS, result.getResultCode());
	} catch (final Exception e) {
	    e.printStackTrace();
	    fail();
	}
	assertFalse(logger.getWarnLog().isEmpty());
    }

    @Test
    public void notAllowedMeansSuccessfulExecution(
	    @Mocked final CheckDependencySnapshotsPhase superMock) {
	final ConditionalCheckDependencySnapshotsPhase phase = new MockUp<ConditionalCheckDependencySnapshotsPhase>() {
	    @Mock
	    ReleaseResult execute(final Invocation inv,
		    final ReleaseDescriptor rd, final ReleaseEnvironment re,
		    final List<MavenProject> projects) throws Throwable {
		return (ReleaseResult) inv.proceed();
	    }
	}.getMockInstance();
	Deencapsulation.setField(phase, "rtr", this.rtr);
	new Expectations() {
	    {
		rtr.isExternalSnapshotsAllowed();
		result = false;
	    }
	};
	try {
	    final ReleaseResult result = phase.execute(
		    (ReleaseDescriptor) null, (ReleaseEnvironment) null,
		    Arrays.asList(root));
	    assertEquals(ReleaseResult.SUCCESS, result.getResultCode());
	} catch (final ReleaseExecutionException | ReleaseFailureException e) {
	    e.printStackTrace();
	    fail();
	}
    }

    @Test
    public void simulateEqualsExecute() {
	final ConditionalCheckDependencySnapshotsPhase phase = new MockUp<ConditionalCheckDependencySnapshotsPhase>() {
	    @Mock
	    ReleaseResult execute(final ReleaseDescriptor rd,
		    final ReleaseEnvironment re,
		    final List<MavenProject> projects) throws Throwable {
		return new ReleaseResult();
	    }
	}.getMockInstance();
	try {
	    final ReleaseResult result = phase.simulate(
		    (ReleaseDescriptor) null, (ReleaseEnvironment) null,
		    (List<MavenProject>) null);
	    assertEquals(ReleaseResult.UNDEFINED, result.getResultCode());
	} catch (final ReleaseExecutionException | ReleaseFailureException e) {
	    fail();
	}
    }

}
