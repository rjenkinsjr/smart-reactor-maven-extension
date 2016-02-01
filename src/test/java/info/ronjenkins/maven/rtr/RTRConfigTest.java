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

import static org.junit.Assert.*;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

public final class RTRConfigTest {

    @Injectable
    MavenSession session;
    @Injectable
    MavenProject project;

    @Test(expected = NullPointerException.class)
    public void isDisabledNullCheck() {
	RTRConfig.isDisabled(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void isSinglePomReactorAllowedNullCheck() {
	RTRConfig.isSinglePomReactorAllowed(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void isReleaseNullCheck() {
	RTRConfig.isRelease(null, null);
    }

    @Test
    public void checkParametersWhenAtLeastOneIsNotNull() {
	Deencapsulation.invoke(RTRConfig.class, "checkParameters", session,
		MavenProject.class);
	Deencapsulation.invoke(RTRConfig.class, "checkParameters",
		MavenSession.class, project);
	Deencapsulation.invoke(RTRConfig.class, "checkParameters", session,
		project);
    }

    @Test
    public void sessionValueUsedWhenProjectIsNull() {
	final String propName = "propName";
	final String inputValue = "1";
	new Expectations() {
	    {
		session.getUserProperties().getProperty(propName);
		result = inputValue;
	    }
	};
	final String value = Deencapsulation.invoke(RTRConfig.class,
		"getProperty", propName, session, MavenProject.class);
	assertEquals(inputValue, value);
    }

    @Test
    public void projectValueUsedWhenSessionIsNull() {
	final String propName = "propName";
	final String inputValue = "1";
	new Expectations() {
	    {
		project.getProperties().getProperty(propName);
		result = inputValue;
	    }
	};
	final String value = Deencapsulation.invoke(RTRConfig.class,
		"getProperty", propName, MavenSession.class, project);
	assertEquals(inputValue, value);
    }

    @Test
    public void nullValueWhenNotDefined() {
	final String propName = "propName";
	final String sessionValue = null;
	final String projectValue = null;
	new Expectations() {
	    {
		session.getUserProperties().getProperty(propName);
		result = sessionValue;
		project.getProperties().getProperty(propName);
		result = projectValue;
	    }
	};
	final String value = Deencapsulation.invoke(RTRConfig.class,
		"getProperty", propName, session, project);
	assertNull(value);
    }

    @Test
    public void sessionPropertyUsedWhenNotDefinedInProject() {
	final String propName = "propName";
	final String sessionValue = "1";
	final String projectValue = null;
	new Expectations() {
	    {
		session.getUserProperties().getProperty(propName);
		result = sessionValue;
		project.getProperties().getProperty(propName);
		result = projectValue;
	    }
	};
	final String value = Deencapsulation.invoke(RTRConfig.class,
		"getProperty", propName, session, project);
	assertEquals(sessionValue, value);
    }

    @Test
    public void projectPropertyUsedWhenNotDefinedInSession() {
	final String propName = "propName";
	final String sessionValue = null;
	final String projectValue = "1";
	new Expectations() {
	    {
		session.getUserProperties().getProperty(propName);
		result = sessionValue;
		project.getProperties().getProperty(propName);
		result = projectValue;
	    }
	};
	final String value = Deencapsulation.invoke(RTRConfig.class,
		"getProperty", propName, session, project);
	assertEquals(projectValue, value);
    }

    @Test
    public void sessionOverridesProjectWhenDefinedInBothPlaces() {
	final String propName = "propName";
	final String sessionValue = "1";
	final String projectValue = "2";
	new Expectations() {
	    {
		session.getUserProperties().getProperty(propName);
		result = sessionValue;
		project.getProperties().getProperty(propName);
		result = projectValue;
	    }
	};
	final String value = Deencapsulation.invoke(RTRConfig.class,
		"getProperty", propName, session, project);
	assertEquals(sessionValue, value);
    }

    @Test
    public void flagIsDefaultWhenPropertyIsNull() {
	new Expectations() {
	    {
		session.getUserProperties().getProperty(anyString);
		result = null;
		project.getProperties().getProperty(anyString);
		result = null;
	    }
	};
	final boolean value = Deencapsulation.invoke(RTRConfig.class,
		"getFlag", "", true, session, project);
	assertTrue(value);
    }

    @Test
    public void flagIsDeterminedBySetProperty() {
	new Expectations() {
	    {
		session.getUserProperties().getProperty(anyString);
		result = "false";
		project.getProperties().getProperty(anyString);
		result = null;
	    }
	};
	final boolean value = Deencapsulation.invoke(RTRConfig.class,
		"getFlag", "", true, session, project);
	assertFalse(value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nonBooleanPropertyValueNotAllowed() {
	new Expectations() {
	    {
		session.getUserProperties().getProperty(anyString);
		result = "abc123";
		project.getProperties().getProperty(anyString);
		result = null;
	    }
	};
	Deencapsulation.invoke(RTRConfig.class, "getFlag", "", true, session,
		project);
    }

    @Test
    public void isDisabled() {
	final boolean propValue = Deencapsulation.getField(RTRConfig.class,
		"DEFAULT_DISABLED");
	new Expectations() {
	    {
		session.getUserProperties().getProperty(anyString);
		result = null;
		project.getProperties().getProperty(anyString);
		result = null;
	    }
	};
	final boolean value = Deencapsulation.invoke(RTRConfig.class,
		"isDisabled", session, project);
	assertEquals(propValue, value);
    }

    @Test
    public void isSinglePomReactorAllowed() {
	final boolean propValue = Deencapsulation.getField(RTRConfig.class,
		"DEFAULT_SINGLE_POM_REACTOR_ALLOWED");
	new Expectations() {
	    {
		session.getUserProperties().getProperty(anyString);
		result = null;
		project.getProperties().getProperty(anyString);
		result = null;
	    }
	};
	final boolean value = Deencapsulation.invoke(RTRConfig.class,
		"isSinglePomReactorAllowed", session, project);
	assertEquals(propValue, value);
    }

    @Test
    public void isRelease() {
	final boolean propValue = Deencapsulation.getField(RTRConfig.class,
		"DEFAULT_RELEASE");
	new Expectations() {
	    {
		session.getUserProperties().getProperty(anyString);
		result = null;
		project.getProperties().getProperty(anyString);
		result = null;
	    }
	};
	final boolean value = Deencapsulation.invoke(RTRConfig.class,
		"isRelease", session, project);
	assertEquals(propValue, value);
    }

}
