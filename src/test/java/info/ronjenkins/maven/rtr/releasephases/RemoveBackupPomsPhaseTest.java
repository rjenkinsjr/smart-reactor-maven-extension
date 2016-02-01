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

import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.junit.Test;

public final class RemoveBackupPomsPhaseTest {

    @Injectable
    MavenSession session;
    @Injectable
    MavenProject root;
    @Injectable
    ReleaseDescriptor rd;
    @Injectable
    ReleaseEnvironment re;

    @Test
    public void coverConstructor() {
	new RemoveBackupPomsPhase();
    }

    @Test
    public void successfulExecution() {
	final RemoveBackupPomsPhase phase = new MockUp<RemoveBackupPomsPhase>() {
	    @Mock
	    ReleaseResult execute(final Invocation inv,
		    final ReleaseDescriptor rd, final ReleaseEnvironment re,
		    final List<MavenProject> projects) throws Throwable {
		return (ReleaseResult) inv.proceed();
	    }
	}.getMockInstance();
	new Expectations() {
	    {
		session.getProjects();
		result = root;
	    }
	};
	try {
	    final ReleaseResult result = phase.execute(this.rd, this.re,
		    session.getProjects());
	    assertEquals(ReleaseResult.SUCCESS, result.getResultCode());
	} catch (final ReleaseExecutionException | ReleaseFailureException e) {
	    fail();
	}
    }

    @Test
    public void simulateEqualsExecute() {
	final RemoveBackupPomsPhase phase = new MockUp<RemoveBackupPomsPhase>() {
	    @Mock
	    ReleaseResult execute(final ReleaseDescriptor rd,
		    final ReleaseEnvironment re,
		    final List<MavenProject> projects) throws Throwable {
		return new ReleaseResult();
	    }
	}.getMockInstance();
	try {
	    final ReleaseResult result = phase.simulate(this.rd, this.re,
		    session.getProjects());
	    assertEquals(ReleaseResult.UNDEFINED, result.getResultCode());
	} catch (final ReleaseExecutionException | ReleaseFailureException e) {
	    fail();
	}
    }

}
