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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.codehaus.plexus.util.ExceptionUtils;

/**
 * Rebuilds the release reactor to take POM filesystem changes into account.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
@Component(role = SmartReactorStep.class, hint = "rebuild-reactor")
public class RebuildReleaseReactor extends AbstractSmartReactorStep {

    @Requirement
    private ProjectBuilder builder;

    @Override
    public void execute(final MavenSession session)
            throws MavenExecutionException {
        final List<MavenProject> reactor = session.getProjects();
        final List<MavenProject> newReactor = new ArrayList<MavenProject>(
                reactor.size());
        File pomFile;
        MavenProject newProject;
        for (final MavenProject project : reactor) {
            pomFile = project.getFile();
            try {
                newProject = this.builder.build(pomFile,
                        session.getProjectBuildingRequest()).getProject();
            } catch (final ProjectBuildingException e) {
                this.logger.error("");
                throw new MavenExecutionException(
                        "Could not rebuild Maven project model\n"
                                + ExceptionUtils.getFullStackTrace(e), pomFile);
            }
            if (project.isExecutionRoot()) {
                newProject.setExecutionRoot(true);
            }
            newReactor.add(newProject);
        }
        // Set the new list of projects, but don't replace the actual list
        // object.
        session.getProjects().clear();
        session.getProjects().addAll(newReactor);
    }

}
