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

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

public final class RTRConfigTest {
  @Injectable
  MavenSession session;
  @Injectable
  MavenProject project;

  @Test
  public void checkParametersWhenAtLeastOneIsNotNull() {
    Deencapsulation.invoke(RTRConfig.class, "checkParameters", this.session,
        MavenProject.class);
    Deencapsulation.invoke(RTRConfig.class, "checkParameters",
        MavenSession.class, this.project);
    Deencapsulation.invoke(RTRConfig.class, "checkParameters", this.session,
        this.project);
  }

  @Test
  public void flagIsDefaultWhenPropertyIsNull() {
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class, "getFlag",
        "", true, this.session, this.project);
    Assert.assertTrue(value);
  }

  @Test
  public void flagIsDeterminedBySetProperty() {
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = "false";
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class, "getFlag",
        "", true, this.session, this.project);
    Assert.assertFalse(value);
  }

  @Test
  public void getProjectVersionPolicyId() {
    final String propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_PROJECTVERSIONPOLICYID");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final String value = Deencapsulation.invoke(RTRConfig.class,
        "getProjectVersionPolicyId", this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test
  public void getReleaseVersion() {
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    Assert.assertNull(Deencapsulation.invoke(RTRConfig.class,
        "getReleaseVersion", this.session, this.project));
  }

  @Test
  public void getTag() {
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    Assert.assertNull(Deencapsulation.invoke(RTRConfig.class, "getTag",
        this.session, this.project));
  }

  @Test
  public void getTagBase() {
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    Assert.assertNull(Deencapsulation.invoke(RTRConfig.class, "getTagBase",
        this.session, this.project));
  }

  @Test
  public void getTagNameFormat() {
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    Assert.assertNull(Deencapsulation.invoke(RTRConfig.class,
        "getTagNameFormat", this.session, this.project));
  }

  @Test
  public void isAddSchema() {
    final boolean propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_ADDSCHEMA");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class,
        "isAddSchema", this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test
  public void isAllowTimestampedSnapshots() {
    final boolean propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_ALLOWTIMESTAMPEDSNAPSHOTS");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class,
        "isAllowTimestampedSnapshots", this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test
  public void isAutoVersionSubmodules() {
    final boolean propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_AUTOVERSIONSUBMODULES");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class,
        "isAutoVersionSubmodules", this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test
  public void isDisabled() {
    final boolean propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_DISABLED");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class, "isDisabled",
        this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test(expected = NullPointerException.class)
  public void isDisabledNullCheck() {
    RTRConfig.isDisabled(null, null);
  }

  @Test
  public void isExternalSnapshotsAllowed() {
    final boolean propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_EXTERNAL_SNAPSHOTS_ALLOWED");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class,
        "isExternalSnapshotsAllowed", this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test
  public void isRelease() {
    final boolean propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_RELEASE");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class, "isRelease",
        this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test(expected = NullPointerException.class)
  public void isReleaseNullCheck() {
    RTRConfig.isRelease(null, null);
  }

  @Test
  public void isSinglePomReactorAllowed() {
    final boolean propValue = Deencapsulation.getField(RTRConfig.class,
        "DEFAULT_SINGLE_POM_REACTOR_ALLOWED");
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = null;
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    final boolean value = Deencapsulation.invoke(RTRConfig.class,
        "isSinglePomReactorAllowed", this.session, this.project);
    Assert.assertEquals(propValue, value);
  }

  @Test(expected = NullPointerException.class)
  public void isSinglePomReactorAllowedNullCheck() {
    RTRConfig.isSinglePomReactorAllowed(null, null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void nonBooleanPropertyValueNotAllowed() {
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(
            this.anyString);
        this.result = "abc123";
        RTRConfigTest.this.project.getProperties().getProperty(this.anyString);
        this.result = null;
      }
    };
    Deencapsulation.invoke(RTRConfig.class, "getFlag", "", true, this.session,
        this.project);
  }

  @Test
  public void nullValueWhenNotDefined() {
    final String propName = "propName";
    final String sessionValue = null;
    final String projectValue = null;
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(propName);
        this.result = sessionValue;
        RTRConfigTest.this.project.getProperties().getProperty(propName);
        this.result = projectValue;
      }
    };
    final String value = Deencapsulation.invoke(RTRConfig.class, "getProperty",
        propName, this.session, this.project);
    Assert.assertNull(value);
  }

  @Test
  public void projectPropertyUsedWhenNotDefinedInSession() {
    final String propName = "propName";
    final String sessionValue = null;
    final String projectValue = "1";
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(propName);
        this.result = sessionValue;
        RTRConfigTest.this.project.getProperties().getProperty(propName);
        this.result = projectValue;
      }
    };
    final String value = Deencapsulation.invoke(RTRConfig.class, "getProperty",
        propName, this.session, this.project);
    Assert.assertEquals(projectValue, value);
  }

  @Test
  public void projectValueUsedWhenSessionIsNull() {
    final String propName = "propName";
    final String inputValue = "1";
    new Expectations() {
      {
        RTRConfigTest.this.project.getProperties().getProperty(propName);
        this.result = inputValue;
      }
    };
    final String value = Deencapsulation.invoke(RTRConfig.class, "getProperty",
        propName, MavenSession.class, this.project);
    Assert.assertEquals(inputValue, value);
  }

  @Test
  public void sessionOverridesProjectWhenDefinedInBothPlaces() {
    final String propName = "propName";
    final String sessionValue = "1";
    final String projectValue = "2";
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(propName);
        this.result = sessionValue;
        RTRConfigTest.this.project.getProperties().getProperty(propName);
        this.result = projectValue;
      }
    };
    final String value = Deencapsulation.invoke(RTRConfig.class, "getProperty",
        propName, this.session, this.project);
    Assert.assertEquals(sessionValue, value);
  }

  @Test
  public void sessionPropertyUsedWhenNotDefinedInProject() {
    final String propName = "propName";
    final String sessionValue = "1";
    final String projectValue = null;
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(propName);
        this.result = sessionValue;
        RTRConfigTest.this.project.getProperties().getProperty(propName);
        this.result = projectValue;
      }
    };
    final String value = Deencapsulation.invoke(RTRConfig.class, "getProperty",
        propName, this.session, this.project);
    Assert.assertEquals(sessionValue, value);
  }

  @Test
  public void sessionValueUsedWhenProjectIsNull() {
    final String propName = "propName";
    final String inputValue = "1";
    new Expectations() {
      {
        RTRConfigTest.this.session.getUserProperties().getProperty(propName);
        this.result = inputValue;
      }
    };
    final String value = Deencapsulation.invoke(RTRConfig.class, "getProperty",
        propName, this.session, MavenProject.class);
    Assert.assertEquals(inputValue, value);
  }
}
