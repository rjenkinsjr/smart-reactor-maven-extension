package info.ronjenkins.maven.rtr;

import static org.junit.Assert.*;
import static util.TestUtils.*;
import info.ronjenkins.maven.rtr.steps.SmartReactorStep;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.junit.Test;

import util.TestLogger;

public final class RTRTest {

    @Injectable
    MavenSession session;
    @Mocked
    RTRConfig config;
    @Injectable
    ProjectBuilder builder;
    @Injectable
    MavenProject root;

    @Test
    public void disabledMeansNoop() {
	final RTR rtr = new MockUp<RTR>() {
	    @Mock
	    void executeSteps(final List<String> steps,
		    final MavenSession session, final RTRComponents components) {
		throw new IllegalStateException("Should not reach here!");
	    }
	}.getMockInstance();
	final TestLogger logger = addLogger(rtr);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		RTRConfig.isDisabled(session, root);
		result = true;
	    }
	};
	try {
	    rtr.afterProjectsRead(session);
	    rtr.afterSessionEnd(session);
	} catch (final MavenExecutionException e) {
	    fail();
	}
	assertTrue(logger.getInfoLog().isEmpty());
    }

    @Test
    public void isReleaseMethodTest() {
	final RTR rtr = new MockUp<RTR>() {
	    @Mock
	    boolean isRelease(final Invocation inv) throws Throwable {
		return (boolean) inv.proceed();
	    }
	}.getMockInstance();
	addLogger(rtr);
	Deencapsulation.setField(rtr, "builder", builder);
	Deencapsulation.setField(rtr, "startSteps", Collections.emptyList());
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		RTRConfig.isRelease(session, root);
		result = true;
	    }
	};
	try {
	    rtr.afterProjectsRead(session);
	} catch (final MavenExecutionException e) {
	    fail();
	}
	assertTrue(rtr.isRelease());
    }

    @Test
    public void successfulExecution(
	    @Injectable Map<String, SmartReactorStep> availableSteps) {
	final RTR rtr = new MockUp<RTR>() {
	}.getMockInstance();
	final TestLogger logger = addLogger(rtr);
	Deencapsulation.setField(rtr, "builder", builder);
	Deencapsulation.setField(rtr, "startSteps", Arrays.asList("step1"));
	Deencapsulation.setField(rtr, "endSteps", Arrays.asList("step2"));
	Deencapsulation.setField(rtr, "availableSteps", availableSteps);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		RTRConfig.isDisabled(session, root);
		result = false;
	    }
	};
	try {
	    rtr.afterProjectsRead(session);
	    rtr.afterSessionEnd(session);
	} catch (final MavenExecutionException e) {
	    e.printStackTrace();
	    fail();
	}
	assertFalse(logger.getInfoLog().isEmpty());
    }

    @Test(expected = MavenExecutionException.class)
    public void nullStepCausesException() throws MavenExecutionException {
	final RTR rtr = new RTR();
	addLogger(rtr);
	Deencapsulation.setField(rtr, "builder", builder);
	Deencapsulation.setField(rtr, "startSteps", Arrays.asList("step1"));
	Deencapsulation.setField(rtr, "availableSteps", Collections.emptyMap());
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		RTRConfig.isDisabled(session, root);
		result = false;
	    }
	};
	rtr.afterProjectsRead(session);
    }

}
