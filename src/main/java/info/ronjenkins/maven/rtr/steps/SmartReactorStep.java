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

import info.ronjenkins.maven.rtr.RTRComponents;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;

/**
 * A step in the execution of the Smart Reactor. The concept is very similar to
 * Maven's "release phases".
 *
 * @author Ronald Jack Jenkins Jr.
 */
public interface SmartReactorStep {
  /**
   * Executes this step.
   *
   * @param session
   *          the session to which this step applies. Not null.
   * @param components
   *          that this step may need. May be null.
   * @throws MavenExecutionException
   *           if any unrecoverable error occurs.
   */
  public void execute(final MavenSession session, final RTRComponents components)
      throws MavenExecutionException;
}
