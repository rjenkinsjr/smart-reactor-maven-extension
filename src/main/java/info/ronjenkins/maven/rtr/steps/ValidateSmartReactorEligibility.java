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
package info.ronjenkins.maven.rtr.steps;

import info.ronjenkins.maven.rtr.RTRComponents;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;
import info.ronjenkins.maven.rtr.reactor.ReactorDependencyGraph;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

/**
 * Validates that this Maven session is eligible to be processed by the Smart
 * Reactor.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "validate-eligibility")
public class ValidateSmartReactorEligibility extends AbstractSmartReactorStep {

    @Override
    public void execute(final MavenSession session,
            final RTRComponents components) throws MavenExecutionException {
        final ReactorDependencyGraph reactorGraph = new ReactorDependencyGraph(
                session);
        if (!reactorGraph.isSmartReactorCompatible()) {
            reactorGraph.error(logger);
            this.logger.error("");
            throw new SmartReactorSanityCheckException(
                    "One or more inter-dependency requirements were not met. See the above graph.");
        }
        reactorGraph.destroy();
    }

}
