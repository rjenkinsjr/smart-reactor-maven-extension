package info.ronjenkins.maven.rtr.reactor;

import static org.junit.Assert.*;

import java.util.Arrays;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

/*
 * Sanity tests to ensure that ReactorDependencyGraphs are constructed/validated correctly.
 */
// @formatter:off
public class RDGSanityTest {

    @Test(expected = IllegalArgumentException.class)
    public void nullSessionNotAllowed() {
	new ReactorDependencyGraph(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void sessionWithNoProjectsNotAllowed(@Mocked final MavenSession session) {
        new Expectations() {{
            session.getProjects(); result = Arrays.asList();
        }};
	new ReactorDependencyGraph(session);
    }

    @Test
    public void graphWithOneNode(@Mocked final MavenSession session) {
	final MavenProject project = new MavenProject();
        new Expectations() {{
            session.getProjects(); result = Arrays.asList(project);
            session.getTopLevelProject(); result = project;
        }};
	final ReactorDependencyGraph rdg = new ReactorDependencyGraph(session);
	assertEquals(rdg.getRoot(), project);
	assertEquals(rdg.getRoot().getGraph(), rdg);
	assertNull(rdg.getRoot().getParent());
	assertTrue(rdg.getRoot().getChildren().isEmpty());
    }

    @Test
    public void graphWithTwoNodes(@Mocked final MavenSession session,
	                          @Mocked final ProjectDependencyGraph pdg) {
	final MavenProject parent = new MockUp<MavenProject>() {
	    @Mock public String getGroupId() { return "test"; }
	    @Mock public String getArtifactId() { return "parent"; }
	    @Mock public String getVersion() { return "1"; }
	}.getMockInstance();
	final MavenProject child = new MockUp<MavenProject>() {
	    @Mock public String getGroupId() { return "test"; }
	    @Mock public String getArtifactId() { return "child"; }
	    @Mock public String getVersion() { return "1"; }
	}.getMockInstance();
	new Expectations() {{
            session.getProjects(); result = Arrays.asList(parent, child);
            session.getTopLevelProject(); result = parent;
            session.getProjectDependencyGraph(); result = pdg;
            pdg.getDownstreamProjects(parent, false); result = Arrays.asList(child);
            pdg.getDownstreamProjects(child, false); result = Arrays.asList();
        }};
	final ReactorDependencyGraph rdg = new ReactorDependencyGraph(session);
	assertEquals(rdg.getRoot(), parent);
	assertEquals(rdg.getRoot().getGraph(), rdg);
	assertNull(rdg.getRoot().getParent());
	assertEquals(rdg.getRoot().getChildren().size(), 1);
	assertEquals(rdg.getRoot().getChildren().get(0), child);
	assertEquals(rdg.getRoot().getChildren().get(0).getGraph(), rdg);
	assertEquals(rdg.getRoot().getChildren().get(0).getParent(), rdg.getRoot());
	assertTrue(rdg.getRoot().getChildren().get(0).getChildren().isEmpty());
    }

}
// @formatter:on
