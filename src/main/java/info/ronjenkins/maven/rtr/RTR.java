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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.graph.DefaultProjectDependencyGraph;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.logging.Logger;

/**
 * The entry point for the Smart Reactor Maven Extension.
 *
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "rtr")
public class RTR extends AbstractMavenLifecycleParticipant {
  private static void checkForRequiredClasses() {
    try {
      new DefaultProjectDependencyGraph(new ArrayList<MavenProject>());
      throw new Exception();
    }
    catch (final Exception e) {
      // Irrelevant.
    }
  }

  @Requirement
  private PlexusContainer                 container;
  @Requirement
  private Logger                          logger;
  @Requirement
  private ProjectBuilder                  builder;
  protected List<String>                  startSteps;
  protected List<String>                  endSuccessSteps;
  protected List<String>                  endFailureSteps;
  @Requirement(role = SmartReactorStep.class)
  protected Map<String, SmartReactorStep> availableSteps;
  private RTRComponents                   components;
  private boolean                         disabledDueToDoubleLoad;
  private boolean                         disabled;
  private boolean                         release;
  private boolean                         backupPomsCreated;
  private boolean                         externalSnapshotsAllowed;

  /**
   * RTR entry point.
   *
   * @param session
   *          the current Maven session, never null.
   */
  @Override
  public void afterProjectsRead(final MavenSession session)
      throws MavenExecutionException {
    // Don't allow this extension to be loaded as a build extension.
    try {
      RTR.checkForRequiredClasses();
    }
    catch (final NoClassDefFoundError e) {
      throw new SmartReactorSanityCheckException(
          "This extension must be loaded as a core extension, not as a build extension.");
    }
    // Don't allow double-execution due to double-classloading.
    this.detectDoubleExecution(session);
    if (this.disabledDueToDoubleLoad) {
      return;
    }
    // Don't do anything if the Smart Reactor is disabled.
    final MavenProject executionRoot = session.getTopLevelProject();
    this.disabled = RTRConfig.isDisabled(session, executionRoot);
    if (this.disabled) {
      return;
    }
    this.release = RTRConfig.isRelease(session, executionRoot);
    this.externalSnapshotsAllowed = RTRConfig.isExternalSnapshotsAllowed(
        session, executionRoot);
    this.logger.info("Assembling smart reactor...");
    this.components = new RTRComponents(this.builder);
    this.executeSteps(this.startSteps, session, this.components);
    // Done. Maven build will proceed from here, none the wiser. ;)
  }

  @Override
  public void afterSessionEnd(final MavenSession session)
      throws MavenExecutionException {
    // Don't allow double-execution due to double-classloading.
    if (this.disabledDueToDoubleLoad) {
      return;
    }
    if (this.disabled) {
      return;
    }
    if (session.getResult().hasExceptions()) {
      this.executeSteps(this.endFailureSteps, session, this.components);
    }
    else {
      this.executeSteps(this.endSuccessSteps, session, this.components);
    }
  }

  private void detectDoubleExecution(final MavenSession session)
      throws SmartReactorSanityCheckException {
    // Get the list of core extensions.
    final List<AbstractMavenLifecycleParticipant> extensions;
    try {
      extensions = this.getExtensions(session);
    }
    catch (final ComponentLookupException e) {
      this.logger.error(ExceptionUtils.getFullStackTrace(e));
      throw new SmartReactorSanityCheckException(
          "Error while checking extension classloaders. Please report this as a bug.");
    }
    // If we find this FQCN more than once, "this" is a double-loaded instance
    // of the libext extension. Disable it so it doesn't cause a failure.
    final String thisFqcn = this.getClass().getName();
    boolean found = false;
    for (final AbstractMavenLifecycleParticipant extension : extensions) {
      if (extension.getClass().getName().equals(thisFqcn)) {
        if (found) {
          // Found twice. Stop searching.
          this.disabledDueToDoubleLoad = true;
          break;
        }
        else {
          // Found once.
          found = true;
        }
      }
    }
  }

  private void executeSteps(final List<String> steps,
      final MavenSession session, final RTRComponents components)
      throws MavenExecutionException {
    SmartReactorStep step;
    for (final String name : steps) {
      step = this.availableSteps.get(name);
      if (step == null) {
        throw new MavenExecutionException("Unable to find step '" + name
            + "' to execute", new IllegalStateException());
      }
      step.execute(session, components);
    }
  }

  // Factored into a separate method for mocking purposes, since JMockit can't
  // mock ClassLoader.
  private List<AbstractMavenLifecycleParticipant> getExtensions(
      final MavenSession session) throws ComponentLookupException {
    final List<AbstractMavenLifecycleParticipant> mvnExtensionsXml = new ArrayList<>();
    // Save the original classloader.
    final ClassLoader originalClassLoader = Thread.currentThread()
        .getContextClassLoader();
    try {
      // Get the libext extensions.
      ClassLoader plexusCore = this.getClass().getClassLoader();
      while (plexusCore.getParent() != null) {
        plexusCore = plexusCore.getParent();
      }
      Thread.currentThread().setContextClassLoader(plexusCore);
      mvnExtensionsXml.addAll(this.container
          .lookupList(AbstractMavenLifecycleParticipant.class));
      // Get the .mvn/extensions.xml extensions.
      for (final MavenProject project : session.getProjects()) {
        final ClassLoader projectRealm = project.getClassRealm();
        if (projectRealm != null) {
          Thread.currentThread().setContextClassLoader(projectRealm);
          mvnExtensionsXml.addAll(this.container
              .lookupList(AbstractMavenLifecycleParticipant.class));
        }
      }
    }
    finally {
      // Restore the original classloader.
      Thread.currentThread().setContextClassLoader(originalClassLoader);
    }
    return mvnExtensionsXml;
  }

  /**
   * Indicates whether or not backup POMs were created by the release process.
   *
   * @return backupPomsCreated true if backup POMs have been created, false
   *         otherwise.
   */
  public boolean isBackupPomsCreated() {
    return this.backupPomsCreated;
  }

  /**
   * Indicates whether or not the Smart Reactor should allow a release reactor
   * containing references to any non-reactor SNAPSHOT artifacts.
   *
   * @return true if allowed, false if prohibited.
   */
  public boolean isExternalSnapshotsAllowed() {
    return this.externalSnapshotsAllowed;
  }

  /**
   * Indicates whether or not a release was requested.
   *
   * @return true if a release was requested, false otherwise.
   */
  public boolean isRelease() {
    return this.release;
  }

  /**
   * Sets the flag that indicates whether or not backup POMs were created by the
   * release process.
   *
   * @param backupPomsCreated
   *          true if backup POMs have been created, false otherwise.
   */
  public void setBackupPomsCreated(final boolean backupPomsCreated) {
    this.backupPomsCreated = backupPomsCreated;
  }
}
