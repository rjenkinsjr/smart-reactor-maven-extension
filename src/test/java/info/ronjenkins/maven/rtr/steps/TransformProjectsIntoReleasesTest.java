package info.ronjenkins.maven.rtr.steps;

import static org.junit.Assert.*;
import static util.TestUtils.*;
import info.ronjenkins.maven.rtr.RTR;
import info.ronjenkins.maven.rtr.steps.release.TransformProjectsIntoReleases;

import java.util.List;
import java.util.Map;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.ReleasePhase;
import org.junit.Test;

import util.TestLogger;

public final class TransformProjectsIntoReleasesTest {

    @Injectable
    MavenSession session;
    @Mocked
    RTR rtr;
    @Mocked
    List<String> rollbackPhases;
    @Mocked
    Map<String, ReleasePhase> availablePhases;
    @Mocked
    ReleaseDescriptor releaseDescriptor;
    @Mocked
    ReleaseEnvironment releaseEnvironment;

    @Test
    public void disabledReleaseMeansNoop() {
	final TransformProjectsIntoReleases step = new TransformProjectsIntoReleases();
	final TestLogger logger = addLoggerAndReleaseDependencies(step, rtr,
		null, null, null, null);
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
	new Verifications() {
	    {
		rtr.isRelease();
		session.getSettings();
		times = 0;
	    }
	};
	assertTrue(logger.getErrorLog().isEmpty());
    }

}
