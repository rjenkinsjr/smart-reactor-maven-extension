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
 * Utility functions.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public final class Util {

    /** Utility class. */
    private Util() {
    }

    /**
     * Returns the value of a given property, forcing JVM (CLI) properties to
     * take precedence over POM-declared properties.
     * 
     * @param prop
     *            the name of the property to search for, not null.
     * @param session
     *            the current Maven session. Null will only consider the
     *            project.
     * @param project
     *            the Maven project in question. Null will only consider the
     *            session.
     * @return null iff the property is not set in the project or the CLI.
     * @throws IllegalArgumentException
     *             if both the session and project are null, or if the property
     *             name is null.
     */
    public static String getProperty(final String prop,
            final MavenSession session, final MavenProject project) {
        if (prop == null) {
            throw new IllegalArgumentException("prop cannot be null");
        }
        if (session == null && project == null) {
            throw new IllegalArgumentException(
                    "session and project cannot both be null");
        }
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

    /**
     * Returns the value of a given boolean property, forcing JVM (CLI)
     * properties to take precedence over POM-declared properties. The
     * property's value must be either "true" or "false" (case-sensitive).
     * 
     * @param prop
     *            the name of the property to search for, not null.
     * @param defaultValue
     *            the value to return if the property is unset.
     * @param session
     *            the current Maven session, not null.
     * @param project
     *            the Maven project in question, not null.
     * @return the property value.
     * @throws IllegalArgumentException
     *             if both the session and project are null, or if the property
     *             name is null.
     * @throws IllegalStateException
     *             if the property is set, and if its value is neither "true"
     *             nor "false".
     */
    public static boolean getBooleanProperty(final String prop,
            final boolean defaultValue, final MavenSession session,
            final MavenProject project) {
        if (prop == null) {
            throw new IllegalArgumentException("prop cannot be null");
        }
        if (session == null && project == null) {
            throw new IllegalArgumentException(
                    "session and project cannot both be null");
        }
        final String rawValue = Util.getProperty(prop, session, project);
        return rawValue == null ? defaultValue : BooleanUtils.toBoolean(
                rawValue, "true", "false");
    }

}
