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
import info.ronjenkins.maven.rtr.RTR;

import java.util.Arrays;
import java.util.List;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.junit.Test;

public final class RemoveBackupPomsPhaseTest {

    @Injectable
    MavenProject root;
    @Injectable
    RTR rtr;

    @Test
    public void backupPomsNotCreatedMeansNoop() {
  final RemoveBackupPomsPhase phase = new RemoveBackupPomsPhase();
  Deencapsulation.setField(phase, "rtr", this.rtr);
  new Expectations() {
      {
    rtr.isBackupPomsCreated();
    result = false;
      }
  };
  try {
      final ReleaseResult result = phase.execute(
        (ReleaseDescriptor) null, (ReleaseEnvironment) null,
        (List<MavenProject>) null);
      assertEquals(ReleaseResult.SUCCESS, result.getResultCode());
  } catch (final ReleaseExecutionException | ReleaseFailureException e) {
      fail();
  }
    }

    @Test
    public void backupPomsCreatedMeansSuccessfulExecution() {
  final RemoveBackupPomsPhase phase = new RemoveBackupPomsPhase();
  Deencapsulation.setField(phase, "rtr", this.rtr);
  new Expectations() {
      {
    rtr.isBackupPomsCreated();
    result = true;
      }
  };
  try {
      final ReleaseResult result = phase.execute(
        (ReleaseDescriptor) null, (ReleaseEnvironment) null,
        Arrays.asList(root));
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
      final ReleaseResult result = phase.simulate(
        (ReleaseDescriptor) null, (ReleaseEnvironment) null,
        (List<MavenProject>) null);
      assertEquals(ReleaseResult.UNDEFINED, result.getResultCode());
  } catch (final ReleaseExecutionException | ReleaseFailureException e) {
      fail();
  }
    }

}
