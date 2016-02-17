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

import info.ronjenkins.maven.rtr.RTRComponents;
import info.ronjenkins.maven.rtr.RTRConfig;
import info.ronjenkins.maven.rtr.steps.SmartReactorStep;

import java.util.List;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Transforms all projects in the Smart Reactor from SNAPSHOTs to non-SNAPSHOTs.
 *
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "transform-poms")
public class TransformProjectsIntoReleases extends
AbstractSmartReactorReleaseStep {

  private List<String> releasePhases;
  private List<String> rollbackPhases;

  @Override
  protected void configureReleaseDescriptor(final MavenSession session,
      final RTRComponents components) {
    final MavenProject executionRoot = session.getTopLevelProject();
    this.releaseDescriptor.setAddSchema(RTRConfig.isAddSchema(session,
        executionRoot));
    this.releaseDescriptor.setAllowTimestampedSnapshots(RTRConfig
        .isAllowTimestampedSnapshots(session, executionRoot));
    this.releaseDescriptor.setAutoVersionSubmodules(RTRConfig
        .isAutoVersionSubmodules(session, executionRoot));
    this.releaseDescriptor.setProjectVersionPolicyId(RTRConfig
        .getProjectVersionPolicyId(session, executionRoot));
    final String releaseVersion = RTRConfig.getReleaseVersion(session,
        executionRoot);
    if (releaseVersion != null) {
      this.releaseDescriptor.setDefaultReleaseVersion(releaseVersion);
    }
    final String tag = RTRConfig.getTag(session, executionRoot);
    if (tag != null) {
      this.releaseDescriptor.setScmReleaseLabel(tag);
    }
    final String tagBase = RTRConfig.getTagBase(session, executionRoot);
    if (tagBase != null) {
      this.releaseDescriptor.setScmTagBase(tagBase);
    }
    final String tagNameFormat = RTRConfig.getTagNameFormat(session,
        executionRoot);
    if (tagNameFormat != null) {
      this.releaseDescriptor.setScmTagNameFormat(tagNameFormat);
    }
  }

  @Override
  public String getAnnouncement() {
    return "Converting reactor projects to releases...";
  }

  @Override
  protected List<String> getReleasePhases() {
    return this.releasePhases;
  }

  @Override
  protected List<String> getRollbackPhases() {
    return this.rollbackPhases;
  }

}
