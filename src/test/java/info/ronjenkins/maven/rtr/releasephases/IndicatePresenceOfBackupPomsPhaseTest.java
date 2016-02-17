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

import info.ronjenkins.maven.rtr.RTR;

import java.util.List;

import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;

import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.ReleaseExecutionException;
import org.apache.maven.shared.release.ReleaseFailureException;
import org.apache.maven.shared.release.ReleaseResult;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.junit.Assert;
import org.junit.Test;

public final class IndicatePresenceOfBackupPomsPhaseTest {

  @Test
  public void simulateEqualsExecute() {
    final IndicatePresenceOfBackupPomsPhase phase = new MockUp<IndicatePresenceOfBackupPomsPhase>() {
      @Mock
      ReleaseResult execute(final ReleaseDescriptor rd,
          final ReleaseEnvironment re, final List<MavenProject> projects)
              throws Throwable {
        return new ReleaseResult();
      }
    }.getMockInstance();
    try {
      final ReleaseResult result = phase.simulate((ReleaseDescriptor) null,
          (ReleaseEnvironment) null, (List<MavenProject>) null);
      Assert.assertEquals(ReleaseResult.UNDEFINED, result.getResultCode());
    } catch (final ReleaseExecutionException | ReleaseFailureException e) {
      Assert.fail();
    }
  }

  @Test
  public void successfulExecution() {
    final IndicatePresenceOfBackupPomsPhase phase = new IndicatePresenceOfBackupPomsPhase();
    final RTR rtr = new RTR();
    Deencapsulation.setField(phase, "rtr", rtr);
    try {
      final ReleaseResult result = phase.execute((ReleaseDescriptor) null,
          (ReleaseEnvironment) null, (List<MavenProject>) null);
      Assert.assertEquals(ReleaseResult.SUCCESS, result.getResultCode());
    } catch (final ReleaseExecutionException | ReleaseFailureException e) {
      Assert.fail();
    }
    Assert.assertTrue(rtr.isBackupPomsCreated());
  }

}
