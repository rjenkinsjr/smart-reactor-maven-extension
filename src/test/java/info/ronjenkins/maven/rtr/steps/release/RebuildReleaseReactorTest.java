package info.ronjenkins.maven.rtr.steps.release;

import static org.junit.Assert.*;
import static util.TestUtils.*;
import info.ronjenkins.maven.rtr.RTR;
import info.ronjenkins.maven.rtr.RTRComponents;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorReleaseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.project.ProjectBuildingResult;
import org.junit.Test;

import util.TestLogger;

public final class RebuildReleaseReactorTest {

    @Injectable
    MavenSession session;
    @Injectable
    RTRComponents components;
    @Mocked
    ProjectBuilder projectBuilder;
    @Mocked
    RTR rtr;

    @Test
    public void disabledReleaseMeansNoop() {
	final RebuildReleaseReactor step = new RebuildReleaseReactor();
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		null, null, null, null, null);
	new Expectations() {
	    {
		rtr.isRelease();
		result = false;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    fail();
	}
	assertTrue(logger.getErrorLog().isEmpty());
    }

    @Test
    public void successfulExecution(@Injectable final MavenProject root,
	    @Injectable final MavenProject child,
	    @Injectable final ProjectBuildingResult rootResult,
	    @Injectable final ProjectBuildingResult childResult) {
	final RebuildReleaseReactor step = new RebuildReleaseReactor();
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		null, null, null, null, null);
	final List<MavenProject> reactor = new ArrayList<MavenProject>();
	reactor.add(root);
	reactor.add(child);
	final File rootFile = new File("rootFile");
	final File childFile = new File("childFile");
	final MavenProject newRoot = new MavenProject();
	final MavenProject newChild = new MavenProject();
	try {
	    new Expectations(rootFile, childFile, newRoot, newChild) {
		{
		    rtr.isRelease();
		    result = true;
		    session.getProjects();
		    result = reactor;
		    root.getFile();
		    result = rootFile;
		    child.getFile();
		    result = childFile;
		    rootFile.equals((File) any);
		    result = false;
		    childFile.equals((File) any);
		    result = false;
		    components.getProjectBuilder();
		    result = projectBuilder;
		    projectBuilder.build(rootFile,
			    session.getProjectBuildingRequest());
		    result = rootResult;
		    projectBuilder.build(childFile,
			    session.getProjectBuildingRequest());
		    result = childResult;
		    rootResult.getProject();
		    result = newRoot;
		    childResult.getProject();
		    result = newChild;
		    root.isExecutionRoot();
		    result = true;
		    child.isExecutionRoot();
		    result = false;
		}
	    };
	} catch (final ProjectBuildingException notPossibleDuringTesting) {
	    notPossibleDuringTesting.printStackTrace();
	    fail();
	}
	try {
	    step.execute(session, components);
	} catch (final MavenExecutionException e) {
	    fail();
	}
	assertTrue(logger.getErrorLog().isEmpty());
	// Do object identity comparisons.
	assertTrue(session.getProjects() == reactor);
	assertFalse(session.getProjects().get(0) == root);
	assertFalse(session.getProjects().get(1) == child);
	assertTrue(session.getProjects().get(0) == newRoot);
	assertTrue(session.getProjects().get(1) == newChild);
	// Do project checks.
	assertTrue(newRoot.isExecutionRoot());
	assertFalse(newChild.isExecutionRoot());
    }

    @Test
    public void exceptionsArePropagated(@Injectable final MavenProject root) {
	final RebuildReleaseReactor step = new RebuildReleaseReactor();
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		null, null, null, null, null);
	final ProjectBuildingException pbe = new ProjectBuildingException("id",
		"error", root.getFile());
	try {
	    new Expectations() {
		{
		    rtr.isRelease();
		    result = true;
		    session.getProjects();
		    result = root;
		    components.getProjectBuilder();
		    result = projectBuilder;
		    projectBuilder.build((File) any,
			    (ProjectBuildingRequest) any);
		    result = pbe;
		}
	    };
	} catch (final ProjectBuildingException notPossibleDuringTesting) {
	    notPossibleDuringTesting.printStackTrace();
	    fail();
	}
	try {
	    step.execute(session, components);
	} catch (final MavenExecutionException e) {
	    assertTrue(e instanceof SmartReactorReleaseException);
	    assertEquals(pbe, e.getCause());
	}
	assertFalse(logger.getErrorLog().isEmpty());
    }
}
