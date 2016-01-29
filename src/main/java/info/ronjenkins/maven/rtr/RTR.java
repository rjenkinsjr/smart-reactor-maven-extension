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

import info.ronjenkins.maven.rtr.steps.SmartReactorStep;

import java.util.List;
import java.util.Map;

import org.apache.maven.AbstractMavenLifecycleParticipant;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.logging.Logger;

/**
 * The entry point for the Smart Reactor Maven Extension.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = AbstractMavenLifecycleParticipant.class, hint = "rtr")
public class RTR extends AbstractMavenLifecycleParticipant {

    @Requirement
    private Logger logger;
    @Requirement
    private ProjectBuilder builder;

    protected List<String> startSteps;
    protected List<String> endSteps;

    @Requirement(role = SmartReactorStep.class)
    protected Map<String, SmartReactorStep> availableSteps;

    private RTRComponents components;
    private boolean release;

    /**
     * RTR entry point.
     * 
     * @param session
     *            the current Maven session, never null.
     */
    @Override
    public void afterProjectsRead(final MavenSession session)
	    throws MavenExecutionException {
	// Don't do anything if the Smart Reactor is disabled.
	final MavenProject executionRoot = session.getTopLevelProject();
	if (RTRConfig.isDisabled(session, executionRoot)) {
	    return;
	}
	this.release = RTRConfig.isRelease(session, executionRoot);
	this.logger.info("Assembling smart reactor...");
	this.components = new RTRComponents(this.builder);
	this.executeSteps(this.startSteps, session, this.components);
	// Done. Maven build will proceed from here, none the wiser. ;)
    }

    @Override
    public void afterSessionEnd(final MavenSession session)
	    throws MavenExecutionException {
	// Don't do anything if the Smart Reactor is disabled.
	final MavenProject executionRoot = session.getTopLevelProject();
	if (RTRConfig.isDisabled(session, executionRoot)) {
	    return;
	}
	this.executeSteps(this.endSteps, session, this.components);
    }

    private void executeSteps(final List<String> steps,
	    final MavenSession session, final RTRComponents components)
	    throws MavenExecutionException {
	SmartReactorStep step;
	for (final String name : steps) {
	    step = this.availableSteps.get(name);
	    if (step == null) {
		throw new MavenExecutionException("Unable to find step '"
			+ name + "' to execute", new IllegalStateException());
	    }
	    step.execute(session, components);
	}
    }

    /**
     * Indicates whether or not a release was requested.
     * 
     * @return true if a release was requested, false otherwise.
     */
    public boolean isRelease() {
	return this.release;
    }

}
