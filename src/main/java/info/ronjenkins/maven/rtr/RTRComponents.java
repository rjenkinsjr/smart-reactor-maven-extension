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
package info.ronjenkins.maven.rtr;

import org.apache.commons.lang.Validate;
import org.apache.maven.project.ProjectBuilder;

/**
 * Components needed throughout the Smart Reactor that can't be accessed
 * directly via Plexus.
 *
 * @author Ronald Jack Jenkins Jr.
 */
// TODO: work on eliminating this class if possible.
public final class RTRComponents {
  private final ProjectBuilder projectBuilder;

  /**
   * Constructor.
   *
   * @param projectBuilder
   *          not null.
   */
  public RTRComponents(final ProjectBuilder projectBuilder) {
    Validate.notNull(projectBuilder, "Project builder is null");
    this.projectBuilder = projectBuilder;
  }

  /**
   * Returns the shared project builder.
   *
   * @return never null.
   */
  public ProjectBuilder getProjectBuilder() {
    return this.projectBuilder;
  }
}
