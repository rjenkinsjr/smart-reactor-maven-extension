package info.ronjenkins.maven.rtr.steps;

import static org.junit.Assert.*;
import static util.TestUtils.*;
import info.ronjenkins.maven.rtr.RTRConfig;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;

import java.util.List;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import util.TestLogger;

public final class PerformSmartReactorSanityChecksTest {

    @Injectable
    MavenSession session;
    @Mocked
    MavenProject root;
    @Mocked
    RTRConfig config;

    @Test
    public void twoProjectReactorAlwaysWorks() {
	final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
	final TestLogger logger = addLogger(step);
	final List<MavenProject> projects = new MockUp<List<MavenProject>>() {
	    // TODO
	    // I tried session.getProjects().size(); result = 2; in the
	    // expectations block but it kept returning 1. Investigate further
	    // and file a bug with JMockit if necessary.
	    @Mock
	    int size() {
		return 2;
	    }
	}.getMockInstance();
	new Expectations() {
	    {
		session.getProjects();
		result = projects;
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
    public void singleProjectNonPomReactorAlwaysWorks() {
	final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
	final TestLogger logger = addLogger(step);
	new Expectations() {
	    {
		session.getProjects().size();
		result = 1;
		session.getTopLevelProject();
		result = root;
		root.getArtifact().getType();
		result = "jar";

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
    public void singleProjectPomReactorWorksIfAllowed() {
	final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
	final TestLogger logger = addLogger(step);
	new Expectations() {
	    {
		session.getProjects().size();
		result = 1;
		session.getTopLevelProject();
		result = root;
		root.getArtifact().getType();
		result = "pom";
		RTRConfig.isSinglePomReactorAllowed(session, root);
		result = true;
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
    public void singleProjectPomReactorFailsIfNotAllowed() {
	final PerformSmartReactorSanityChecks step = new PerformSmartReactorSanityChecks();
	final TestLogger logger = addLogger(step);
	new Expectations() {
	    {
		session.getProjects().size();
		result = 1;
		session.getTopLevelProject();
		result = root;
		root.getArtifact().getType();
		result = "pom";
		RTRConfig.isSinglePomReactorAllowed(session, root);
		result = false;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    assertTrue(e instanceof SmartReactorSanityCheckException);
	}
	assertFalse(logger.getErrorLog().isEmpty());
    }

}
