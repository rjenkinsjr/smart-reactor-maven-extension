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
package util;

import info.ronjenkins.maven.rtr.RTR;
import info.ronjenkins.maven.rtr.steps.SmartReactorStep;
import info.ronjenkins.maven.rtr.steps.release.AbstractSmartReactorReleaseStep;

import java.util.Map;

import mockit.Deencapsulation;

import org.apache.commons.lang.Validate;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.ReleasePhase;

/**
 * Utility functions to facilitate testing.
 *
 * @author Ronald Jack Jenkins Jr.
 */
public final class TestUtils {

  /**
   * Adds a test logger to a release phase for testing.
   *
   * @param phase
   *          not null.
   * @return never null.
   */
  public static TestLogger addLogger(final ReleasePhase phase) {
    Validate.notNull(phase, "phase is null");
    final TestLogger logger = new TestLogger();
    Deencapsulation.setField(phase, "logger", logger);
    return logger;
  }

  /**
   * Adds a test logger to an RTR for testing.
   *
   * @param step
   *          not null.
   * @return never null.
   */
  public static TestLogger addLogger(final RTR rtr) {
    Validate.notNull(rtr, "rtr is null");
    final TestLogger logger = new TestLogger();
    Deencapsulation.setField(rtr, "logger", logger);
    return logger;
  }

  /**
   * Adds a test logger to a smart reactor step for testing.
   *
   * @param step
   *          not null.
   * @return never null.
   */
  public static TestLogger addLogger(final SmartReactorStep step) {
    Validate.notNull(step, "step is null");
    final TestLogger logger = new TestLogger();
    Deencapsulation.setField(step, "logger", logger);
    return logger;
  }

  /**
   * Adds a test logger and other dependencies to a smart reactor release step
   * for testing.
   *
   * @param step
   *          not null.
   * @param rtr
   *          not null.
   * @param availablePhases
   *          can be null.
   * @param releaseDescriptor
   *          can be null.
   * @param releaseEnvironment
   *          can be null.
   * @return never null.
   */
  public static TestLogger addLoggerAndReleaseDependencies(
      final AbstractSmartReactorReleaseStep step, final RTR rtr,
      final Map<String, ReleasePhase> availablePhases,
      final ReleaseDescriptor releaseDescriptor,
      final ReleaseEnvironment releaseEnvironment) {
    Validate.notNull(step, "step is null");
    Validate.notNull(rtr, "rtr is null");
    final TestLogger logger = TestUtils.addLogger(step);
    Deencapsulation.setField(step, "rtr", rtr);
    Deencapsulation.setField(step, "availablePhases", availablePhases);
    Deencapsulation.setField(step, "releaseDescriptor", releaseDescriptor);
    Deencapsulation.setField(step, "releaseEnvironment", releaseEnvironment);
    return logger;
  }

  /** Uninstantiable. */
  private TestUtils() {
  }

}
