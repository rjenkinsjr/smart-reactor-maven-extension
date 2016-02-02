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

import java.util.List;

import info.ronjenkins.maven.rtr.steps.SmartReactorStep;

import org.codehaus.plexus.component.annotations.Component;

/**
 * Cleans up/rolls back a release build.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "post-release")
public class DoPostRelease extends AbstractSmartReactorReleaseStep {

    private List<String> releasePhases;
    private List<String> rollbackPhases;

    @Override
    public String getAnnouncement() {
	return "Performing post-release cleanup...";
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
