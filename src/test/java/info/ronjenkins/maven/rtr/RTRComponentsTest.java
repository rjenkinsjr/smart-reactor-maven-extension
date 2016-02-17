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
import mockit.Injectable;

import org.apache.maven.project.ProjectBuilder;
import org.junit.Test;

public final class RTRComponentsTest {

    @Injectable
    ProjectBuilder projectBuilder;

    @Test(expected = IllegalArgumentException.class)
    public void noNullProjectBuilder() {
	new RTRComponents(null);
    }

    @Test
    public void projectBuilder() {
	final RTRComponents rtrc = new RTRComponents(projectBuilder);
	assertEquals(projectBuilder, rtrc.getProjectBuilder());
    }

}
