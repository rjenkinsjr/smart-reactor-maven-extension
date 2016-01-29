package info.ronjenkins.maven.rtr.steps;

import static org.junit.Assert.*;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import util.TestLogger;

public final class ValidateSmartReactorEligibilityTest {

    @Injectable
    MavenSession session;
    @Injectable
    MavenProject root;

    @Test
    public void nonSnapshotRootIsNotEligible() {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = new TestLogger();
	Deencapsulation.setField(step, logger);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		root.getArtifact().isSnapshot();
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

    @Test
    public void singleSnapshotReactorIsEligible() {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = new TestLogger();
	Deencapsulation.setField(step, logger);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		root.getArtifact().isSnapshot();
		result = true;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    fail(ExceptionUtils.getFullStackTrace(e));
	}
	assertTrue(logger.getErrorLog().isEmpty());
    }

}
