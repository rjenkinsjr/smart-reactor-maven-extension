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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

/**
 * Configuration parser/constants for the Smart Reactor.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public final class RTRConfig {

    public static final String PROP_DISABLED = "rtr.disabled";
    public static final boolean DEFAULT_DISABLED = false;
    public static final String PROP_SINGLE_POM_REACTOR_ALLOWED = "rtr.allowSinglePomReactor";
    public static final boolean DEFAULT_SINGLE_POM_REACTOR_ALLOWED = false;
    public static final String PROP_EXTERNAL_SNAPSHOTS_ALLOWED = "rtr.allowExternalSnapshots";
    public static final boolean DEFAULT_EXTERNAL_SNAPSHOTS_ALLOWED = false;
    public static final String PROP_RELEASE = "rtr.release";
    public static final boolean DEFAULT_RELEASE = false;

    public static final String PROP_ADDSCHEMA = "addSchema";
    public static final boolean DEFAULT_ADDSCHEMA = true;
    public static final String PROP_ALLOWTIMESTAMPEDSNAPSHOTS = "allowTimestampedSnapshots";
    public static final boolean DEFAULT_ALLOWTIMESTAMPEDSNAPSHOTS = false;
    public static final String PROP_AUTOVERSIONSUBMODULES = "autoVersionSubmodules";
    public static final boolean DEFAULT_AUTOVERSIONSUBMODULES = false;
    public static final String PROP_PROJECTVERSIONPOLICYID = "projectVersionPolicyId";
    public static final String DEFAULT_PROJECTVERSIONPOLICYID = "default";
    public static final String PROP_RELEASEVERSION = "releaseVersion";
    public static final String PROP_TAG = "tag";
    public static final String PROP_TAGBASE = "tagBase";
    public static final String PROP_TAGNAMEFORMAT = "tagNameFormat";

    /**
     * Indicates whether or not the Smart Reactor is disabled.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return true if disabled, false if enabled.
     */
    public static boolean isDisabled(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getFlag(PROP_DISABLED, DEFAULT_DISABLED, session, project);
    }

    /**
     * Indicates whether or not the Smart Reactor should allow a reactor
     * containing a single POM-packaging project.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return true if allowed, false if prohibited.
     */
    public static boolean isSinglePomReactorAllowed(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getFlag(PROP_SINGLE_POM_REACTOR_ALLOWED,
    DEFAULT_SINGLE_POM_REACTOR_ALLOWED, session, project);
    }

    /**
     * Indicates whether or not the Smart Reactor should allow a release reactor
     * containing references to any non-reactor SNAPSHOT artifacts.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return true if allowed, false if prohibited.
     */
    public static boolean isExternalSnapshotsAllowed(
      final MavenSession session, final MavenProject project) {
  checkParameters(session, project);
  return getFlag(PROP_EXTERNAL_SNAPSHOTS_ALLOWED,
    DEFAULT_EXTERNAL_SNAPSHOTS_ALLOWED, session, project);
    }

    /**
     * Indicates whether or not a release was requested.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return true if a release was requested, false otherwise.
     */
    public static boolean isRelease(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getFlag(PROP_RELEASE, DEFAULT_RELEASE, session, project);
    }

    /**
     * Returns the "addSchema" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return true if set in the session or given project, false if unset.
     */
    public static boolean isAddSchema(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getFlag(PROP_ADDSCHEMA, DEFAULT_ADDSCHEMA, session, project);
    }

    /**
     * Returns the "allowTimestampedSnapshots" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return true if set in the session or given project, false if unset.
     */
    public static boolean isAllowTimestampedSnapshots(
      final MavenSession session, final MavenProject project) {
  checkParameters(session, project);
  return getFlag(PROP_ALLOWTIMESTAMPEDSNAPSHOTS,
    DEFAULT_ALLOWTIMESTAMPEDSNAPSHOTS, session, project);
    }

    /**
     * Returns the "autoVersionSubmodules" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return true if set in the session or given project, false if unset.
     */
    public static boolean isAutoVersionSubmodules(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getFlag(PROP_AUTOVERSIONSUBMODULES,
    DEFAULT_AUTOVERSIONSUBMODULES, session, project);
    }

    /**
     * Returns the "projectVersionPolicyId" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return never null. "default" if unset anywhere.
     */
    public static String getProjectVersionPolicyId(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return StringUtils.defaultString(
    getProperty(PROP_PROJECTVERSIONPOLICYID, session, project),
    DEFAULT_PROJECTVERSIONPOLICYID);
    }

    /**
     * Returns the "releaseVersion" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return null iff unset anywhere.
     */
    public static String getReleaseVersion(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getProperty(PROP_RELEASEVERSION, session, project);
    }

    /**
     * Returns the "tag" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return null iff unset anywhere.
     */
    public static String getTag(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getProperty(PROP_TAG, session, project);
    }

    /**
     * Returns the "tagBase" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return null iff unset anywhere.
     */
    public static String getTagBase(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getProperty(PROP_TAGBASE, session, project);
    }

    /**
     * Returns the "tagNameFormat" property.
     * 
     * @param session
     *            the Maven session.
     * @param project
     *            a project that may have this property set.
     * @return null iff unset anywhere.
     */
    public static String getTagNameFormat(final MavenSession session,
      final MavenProject project) {
  checkParameters(session, project);
  return getProperty(PROP_TAGNAMEFORMAT, session, project);
    }

    /*
     * Private utility methods.
     */

    private static String getProperty(final String prop,
      final MavenSession session, final MavenProject project) {
  if (session == null) {
      return project.getProperties().getProperty(prop);
  } else if (project == null) {
      return session.getUserProperties().getProperty(prop);
  } else {
      return StringUtils.defaultString(session.getUserProperties()
        .getProperty(prop),
        project.getProperties().getProperty(prop));
  }
    }

    private static boolean getFlag(final String prop,
      final boolean defaultValue, final MavenSession session,
      final MavenProject project) {
  final String rawValue = getProperty(prop, session, project);
  return rawValue == null ? defaultValue : BooleanUtils.toBoolean(
    rawValue, "true", "false");
    }

    private static void checkParameters(final MavenSession session,
      final MavenProject project) {
  if (session == null && project == null) {
      throw new NullPointerException(
        "session and project cannot both be null");
  }
    }

    /* This class is not instantiable. */
    private RTRConfig() {
    }

}
