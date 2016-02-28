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
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class TransformProjectsIntoReleasesTest {
  @Injectable
  MavenSession session;
  @Injectable
  MavenProject executionRoot;
  @Mocked
  RTRConfig    config;

  @Test
  public void configureReleaseDescriptor() {
    final TransformProjectsIntoReleases tpir = new TransformProjectsIntoReleases();
    final ReleaseDescriptor rd = new ReleaseDescriptor();
    Deencapsulation.setField(tpir, "releaseDescriptor", rd);
    new Expectations() {
      {
        RTRConfig.isAddSchema(TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = true;
        RTRConfig.isAllowTimestampedSnapshots(
            TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = true;
        RTRConfig.isAutoVersionSubmodules(
            TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = true;
        RTRConfig.getProjectVersionPolicyId(
            TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = "abc123";
        RTRConfig.getReleaseVersion(
            TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = "rv";
        RTRConfig.getTag(TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = "tag";
        RTRConfig.getTagBase(TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = "tagBase";
        RTRConfig.getTagNameFormat(
            TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = "tagNameFormat";
      }
    };
    Deencapsulation.invoke(tpir, "configureReleaseDescriptor", this.session,
        RTRComponents.class);
    Assert.assertTrue(rd.isAddSchema());
    Assert.assertTrue(rd.isAllowTimestampedSnapshots());
    Assert.assertTrue(rd.isAutoVersionSubmodules());
    Assert.assertEquals("abc123", rd.getProjectVersionPolicyId());
    Assert.assertEquals("rv", rd.getDefaultReleaseVersion());
    Assert.assertEquals("tag", rd.getScmReleaseLabel());
    Assert.assertEquals("tagBase", rd.getScmTagBase());
    Assert.assertEquals("tagNameFormat", rd.getScmTagNameFormat());
  }

  @Test
  public void configureReleaseDescriptorWithoutOptionalParameters(
      @Mocked final ReleaseDescriptor rd) {
    final TransformProjectsIntoReleases tpir = new TransformProjectsIntoReleases();
    Deencapsulation.setField(tpir, "releaseDescriptor", rd);
    new Expectations() {
      {
        RTRConfig.getReleaseVersion(
            TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = null;
        RTRConfig.getTag(TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = null;
        RTRConfig.getTagBase(TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = null;
        RTRConfig.getTagNameFormat(
            TransformProjectsIntoReleasesTest.this.session,
            TransformProjectsIntoReleasesTest.this.executionRoot);
        this.result = null;
      }
    };
    Deencapsulation.invoke(tpir, "configureReleaseDescriptor", this.session,
        RTRComponents.class);
    new Verifications() {
      {
        rd.setDefaultReleaseVersion(this.anyString);
        this.times = 0;
        rd.setScmReleaseLabel(this.anyString);
        this.times = 0;
        rd.setScmTagBase(this.anyString);
        this.times = 0;
        rd.setScmTagNameFormat(this.anyString);
        this.times = 0;
      }
    };
  }
}
