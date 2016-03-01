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

import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;
import info.ronjenkins.maven.rtr.steps.SmartReactorStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.Assert;
import org.junit.Test;

import util.TestLogger;
import util.TestUtils;

public final class RTRTest {
  @Injectable
  MavenSession    session;
  @Mocked
  RTRConfig       config;
  @Injectable
  PlexusContainer container;
  @Injectable
  ProjectBuilder  builder;
  @Injectable
  MavenProject    root;

  @Test
  public void booleanMethodTests() {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      List<AbstractMavenLifecycleParticipant> getExtensions(
          final MavenSession session) {
        return Collections.emptyList();
      }

      @Mock
      boolean isRelease(final Invocation inv) throws Throwable {
        return (boolean) inv.proceed();
      }
    }.getMockInstance();
    TestUtils.addLogger(rtr);
    Deencapsulation.setField(rtr, "container", this.container);
    Deencapsulation.setField(rtr, "builder", this.builder);
    Deencapsulation.setField(rtr, "startSteps", Collections.emptyList());
    new Expectations() {
      {
        RTRTest.this.session.getTopLevelProject();
        this.result = RTRTest.this.root;
        RTRConfig.isRelease(RTRTest.this.session, RTRTest.this.root);
        this.result = true;
        RTRConfig.isExternalSnapshotsAllowed(RTRTest.this.session,
            RTRTest.this.root);
        this.result = true;
      }
    };
    try {
      rtr.afterProjectsRead(this.session);
    }
    catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(rtr.isRelease());
    Assert.assertTrue(rtr.isExternalSnapshotsAllowed());
  }

  @Test(expected = MavenExecutionException.class)
  public void buildExtensionCausesException() throws MavenExecutionException {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      void afterProjectsRead(final Invocation inv, final MavenSession session)
          throws Throwable {
        inv.proceed(session);
      }

      @Mock
      void checkForRequiredClasses() {
        throw new NoClassDefFoundError();
      }
    }.getMockInstance();
    rtr.afterProjectsRead(this.session);
  }

  @Test
  public void classCheckTest() {
    Deencapsulation.invoke(RTR.class, "checkForRequiredClasses");
  }

  @Test
  public void disabledMeansNoop() {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      void executeSteps(final List<String> steps, final MavenSession session,
          final RTRComponents components) {
        throw new IllegalStateException("Should not reach here!");
      }

      @Mock
      List<AbstractMavenLifecycleParticipant> getExtensions(
          final MavenSession session) {
        return Collections.emptyList();
      }
    }.getMockInstance();
    final TestLogger logger = TestUtils.addLogger(rtr);
    Deencapsulation.setField(rtr, "container", this.container);
    new Expectations() {
      {
        RTRTest.this.session.getTopLevelProject();
        this.result = RTRTest.this.root;
        RTRConfig.isDisabled(RTRTest.this.session, RTRTest.this.root);
        this.result = true;
      }
    };
    try {
      rtr.afterProjectsRead(this.session);
      rtr.afterSessionEnd(this.session);
    }
    catch (final MavenExecutionException e) {
      Assert.fail();
    }
    Assert.assertTrue(logger.getInfoLog().isEmpty());
  }

  @Test
  public void doubleDetectionComponentLookupException(
      @Injectable final Map<String, SmartReactorStep> availableSteps) {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      List<AbstractMavenLifecycleParticipant> getExtensions(
          final MavenSession session) throws ComponentLookupException {
        throw new ComponentLookupException("test", "test", "test");
      }
    }.getMockInstance();
    final TestLogger logger = TestUtils.addLogger(rtr);
    Deencapsulation.setField(rtr, "container", this.container);
    try {
      rtr.afterProjectsRead(this.session);
    }
    catch (final MavenExecutionException e) {
      Assert.assertTrue(e instanceof SmartReactorSanityCheckException);
    }
    Assert.assertTrue(logger.getInfoLog().isEmpty());
  }

  @Test
  public void doubleLoadMeansNoExecution() {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      List<AbstractMavenLifecycleParticipant> getExtensions(
          final MavenSession session) {
        final List<AbstractMavenLifecycleParticipant> extensions = new ArrayList<>();
        extensions.add(new RTR());
        extensions.add(new RTR());
        return extensions;
      }
    }.getMockInstance();
    final TestLogger logger = TestUtils.addLogger(rtr);
    Deencapsulation.setField(rtr, "container", this.container);
    try {
      rtr.afterProjectsRead(this.session);
      rtr.afterSessionEnd(this.session);
    }
    catch (final MavenExecutionException e) {
      e.printStackTrace();
      Assert.fail();
    }
    Assert.assertTrue(logger.getInfoLog().isEmpty());
  }

  @Test
  public void failedExecution(
      @Injectable final Map<String, SmartReactorStep> availableSteps) {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      List<AbstractMavenLifecycleParticipant> getExtensions(
          final MavenSession session) {
        return Collections.emptyList();
      }
    }.getMockInstance();
    final TestLogger logger = TestUtils.addLogger(rtr);
    Deencapsulation.setField(rtr, "container", this.container);
    Deencapsulation.setField(rtr, "builder", this.builder);
    Deencapsulation.setField(rtr, "startSteps", Arrays.asList("step1"));
    Deencapsulation.setField(rtr, "endFailureSteps", Arrays.asList("step2"));
    Deencapsulation.setField(rtr, "availableSteps", availableSteps);
    new Expectations() {
      {
        RTRTest.this.session.getTopLevelProject();
        this.result = RTRTest.this.root;
        RTRTest.this.session.getResult().hasExceptions();
        this.result = true;
        RTRConfig.isDisabled(RTRTest.this.session, RTRTest.this.root);
        this.result = false;
      }
    };
    try {
      rtr.afterProjectsRead(this.session);
      rtr.afterSessionEnd(this.session);
    }
    catch (final MavenExecutionException e) {
      e.printStackTrace();
      Assert.fail();
    }
    Assert.assertFalse(logger.getInfoLog().isEmpty());
  }

  @Test(expected = MavenExecutionException.class)
  public void nullStepCausesException() throws MavenExecutionException {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      void afterProjectsRead(final Invocation inv, final MavenSession session) {
        inv.proceed(session);
      }

      @Mock
      void executeSteps(final Invocation inv, final List<String> steps,
          final MavenSession session, final RTRComponents components)
          throws MavenExecutionException {
        inv.proceed(steps, session, components);
      }

      @Mock
      List<AbstractMavenLifecycleParticipant> getExtensions(
          final MavenSession session) {
        return Collections.emptyList();
      }
    }.getMockInstance();
    TestUtils.addLogger(rtr);
    Deencapsulation.setField(rtr, "container", this.container);
    Deencapsulation.setField(rtr, "builder", this.builder);
    Deencapsulation.setField(rtr, "startSteps", Arrays.asList("step1"));
    Deencapsulation.setField(rtr, "availableSteps", Collections.emptyMap());
    new Expectations() {
      {
        RTRTest.this.session.getTopLevelProject();
        this.result = RTRTest.this.root;
        RTRConfig.isDisabled(RTRTest.this.session, RTRTest.this.root);
        this.result = false;
      }
    };
    rtr.afterProjectsRead(this.session);
  }

  @Test
  public void successfulExecution(
      @Injectable final Map<String, SmartReactorStep> availableSteps) {
    final RTR rtr = new MockUp<RTR>() {
      @Mock
      List<AbstractMavenLifecycleParticipant> getExtensions(
          final MavenSession session) {
        final List<AbstractMavenLifecycleParticipant> extensions = new ArrayList<>();
        extensions.add(new RTR());
        extensions.add(new AbstractMavenLifecycleParticipant() {});
        return extensions;
      }
    }.getMockInstance();
    final TestLogger logger = TestUtils.addLogger(rtr);
    Deencapsulation.setField(rtr, "container", this.container);
    Deencapsulation.setField(rtr, "builder", this.builder);
    Deencapsulation.setField(rtr, "startSteps", Arrays.asList("step1"));
    Deencapsulation.setField(rtr, "endSuccessSteps", Arrays.asList("step2"));
    Deencapsulation.setField(rtr, "availableSteps", availableSteps);
    new Expectations() {
      {
        RTRTest.this.session.getTopLevelProject();
        this.result = RTRTest.this.root;
        RTRTest.this.session.getResult().hasExceptions();
        this.result = false;
        RTRConfig.isDisabled(RTRTest.this.session, RTRTest.this.root);
        this.result = false;
      }
    };
    try {
      rtr.afterProjectsRead(this.session);
      rtr.afterSessionEnd(this.session);
    }
    catch (final MavenExecutionException e) {
      e.printStackTrace();
      Assert.fail();
    }
    Assert.assertFalse(logger.getInfoLog().isEmpty());
  }
}
