package info.ronjenkins.maven.rtr.reactor;

import static info.ronjenkins.maven.rtr.TestUtils.*;
import static org.junit.Assert.*;
import info.ronjenkins.maven.rtr.reactor.ReactorDependencyGraph.Node;

import java.util.Arrays;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

/*
 * Sanity tests to ensure that ReactorDependencyGraphs are constructed/validated correctly.
 */
// @formatter:off
public class ReactorDependencyGraphTest {

    @Injectable MavenSession session;

    @Test(expected = IllegalArgumentException.class)
    public void nullSessionNotAllowed() {
	new ReactorDependencyGraph(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void sessionWithNoProjectsNotAllowed() {
	new ReactorDependencyGraph(session);
    }
    
    @Test
    public void sessionWithSingleNode(@Mocked final ProjectDependencyGraph pdg) {
	final MavenProject node = mockMavenProject("test", "project", "1");
	new Expectations() {{
            session.getProjects(); result = node;
            session.getTopLevelProject(); result = node;
        }};
	final ReactorDependencyGraph rdg = new ReactorDependencyGraph(session);
	new Verifications() {{
	    session.getProjects();
	    session.getTopLevelProject();
	    pdg.getUpstreamProjects(node, false);
	}};
	final Node actualRoot = rdg.getRoot();
	assertEquals(node, actualRoot);
	assertEquals(rdg, actualRoot.getGraph());
	assertNull(actualRoot.getParent());
	assertTrue(actualRoot.getChildren().isEmpty());
    }
    
    @Test
    public void sessionWithTwoNodes(@Mocked final ProjectDependencyGraph pdg) {
	final MavenProject parent = mockMavenProject("test", "parent", "1");
	final MavenProject child = mockMavenProject("test", "child", "1");
	new Expectations() {{
            session.getProjects(); result = Arrays.asList(parent, child);
            session.getTopLevelProject(); result = parent;
            pdg.getUpstreamProjects(parent, false); result = child;
            pdg.getUpstreamProjects(child, false); result = Arrays.asList();
        }};
	final ReactorDependencyGraph rdg = new ReactorDependencyGraph(session);
	new Verifications() {{
	    session.getProjects();
	    session.getTopLevelProject();
	    pdg.getUpstreamProjects(parent, false);
	    pdg.getUpstreamProjects(child, false);
	}};
	final Node actualParent = rdg.getRoot();
	assertEquals(parent, actualParent);
	assertEquals(rdg, actualParent.getGraph());
	assertNull(actualParent.getParent());
	assertEquals(1, actualParent.getChildren().size());
	final Node actualChild = actualParent.getChildren().get(0);
	assertEquals(child, actualChild);
	assertEquals(rdg, actualChild.getGraph());
	assertEquals(actualParent, actualChild.getParent());
	assertTrue(actualChild.getChildren().isEmpty());
    }
    
    @Test
    public void sessionWithParentAndTwoChildren(@Mocked final ProjectDependencyGraph pdg) {
	final MavenProject parent = mockMavenProject("test", "parent", "1");
	final MavenProject childA = mockMavenProject("test", "childA", "1");
	final MavenProject childB = mockMavenProject("test", "childB", "1");
	new Expectations() {{
            session.getProjects(); result = Arrays.asList(parent, childA, childB);
            session.getTopLevelProject(); result = parent;
            pdg.getUpstreamProjects(parent, false); result = Arrays.asList(childA, childB);
            pdg.getUpstreamProjects(childA, false); result = Arrays.asList();
            pdg.getUpstreamProjects(childB, false); result = Arrays.asList();
        }};
	final ReactorDependencyGraph rdg = new ReactorDependencyGraph(session);
	new Verifications() {{
	    session.getProjects();
	    session.getTopLevelProject();
	    pdg.getUpstreamProjects(parent, false);
	    pdg.getUpstreamProjects(childA, false);
	    pdg.getUpstreamProjects(childB, false);
	}};
	final Node actualParent = rdg.getRoot();
	assertEquals(parent, actualParent);
	assertEquals(rdg, actualParent.getGraph());
	assertNull(actualParent.getParent());
	assertEquals(2, actualParent.getChildren().size());
	final Node actualChildA = actualParent.getChildren().get(0);
	assertEquals(childA, actualChildA);
	assertEquals(rdg, actualChildA.getGraph());
	assertEquals(actualParent, actualChildA.getParent());
	assertTrue(actualChildA.getChildren().isEmpty());
	final Node actualChildB = actualParent.getChildren().get(1);
	assertEquals(childB, actualChildB);
	assertEquals(rdg, actualChildB.getGraph());
	assertEquals(actualParent, actualChildB.getParent());
	assertTrue(actualChildB.getChildren().isEmpty());
    }
    
    @Test
    public void sessionWithParentChildAndGrandchild(@Mocked final ProjectDependencyGraph pdg) {
	final MavenProject parent = mockMavenProject("test", "parent", "1");
	final MavenProject child = mockMavenProject("test", "child", "1");
	final MavenProject grandchild = mockMavenProject("test", "grandchild", "1");
	new Expectations() {{
            session.getProjects(); result = Arrays.asList(parent, child, grandchild);
            session.getTopLevelProject(); result = parent;
            pdg.getUpstreamProjects(parent, false); result = Arrays.asList(child);
            pdg.getUpstreamProjects(child, false); result = Arrays.asList(grandchild);
            pdg.getUpstreamProjects(grandchild, false); result = Arrays.asList();
        }};
	final ReactorDependencyGraph rdg = new ReactorDependencyGraph(session);
	new Verifications() {{
	    session.getProjects();
	    session.getTopLevelProject();
	    pdg.getUpstreamProjects(parent, false);
	    pdg.getUpstreamProjects(child, false);
	    pdg.getUpstreamProjects(grandchild, false);
	}};
	final Node actualParent = rdg.getRoot();
	assertEquals(parent, actualParent);
	assertEquals(rdg, actualParent.getGraph());
	assertNull(actualParent.getParent());
	assertEquals(1, actualParent.getChildren().size());
	final Node actualChild = actualParent.getChildren().get(0);
	assertEquals(child, actualChild);
	assertEquals(rdg, actualChild.getGraph());
	assertEquals(actualParent, actualChild.getParent());
	assertEquals(1, actualChild.getChildren().size());
	final Node actualGrandchild = actualChild.getChildren().get(0);
	assertEquals(grandchild, actualGrandchild);
	assertEquals(rdg, actualGrandchild.getGraph());
	assertEquals(actualChild, actualGrandchild.getParent());
	assertTrue(actualGrandchild.getChildren().isEmpty());
    }

}
// @formatter:on
