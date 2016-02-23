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

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Assert;
import org.junit.Test;

public final class BuildSmartReactorTest {
  @Injectable
  MavenSession session;

  @Test
  public void onlySnapshotsRemain(@Injectable final MavenProject snapshot,
      @Injectable final MavenProject release) {
    final BuildSmartReactor step = new BuildSmartReactor();
    final List<MavenProject> projects = new ArrayList<MavenProject>();
    projects.add(snapshot);
    projects.add(release);
    new Expectations() {
      {
        BuildSmartReactorTest.this.session.getProjects();
        this.result = projects;
        snapshot.getArtifact().isSnapshot();
        this.result = true;
        release.getArtifact().isSnapshot();
        this.result = false;
      }
    };
    step.execute(this.session, null);
    Assert.assertEquals(projects, this.session.getProjects());
    Assert.assertEquals(1, this.session.getProjects().size());
    Assert.assertEquals(snapshot, this.session.getProjects().get(0));
  }
}
