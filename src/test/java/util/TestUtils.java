package util;

import info.ronjenkins.maven.rtr.RTR;
import info.ronjenkins.maven.rtr.steps.SmartReactorStep;
import info.ronjenkins.maven.rtr.steps.release.AbstractSmartReactorReleaseStep;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.apache.maven.shared.release.env.ReleaseEnvironment;
import org.apache.maven.shared.release.phase.ReleasePhase;

/**
 * Utility functions to facilitate testing.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public final class TestUtils {

    /**
     * Adds a test logger to a smart reactor step for testing.
     * 
     * @param step
     *            not null.
     * @return never null.
     */
    public static TestLogger addLogger(final SmartReactorStep step) {
	Validate.notNull(step, "step is null");
	final TestLogger logger = new TestLogger();
	Deencapsulation.setField(step, "logger", logger);
	return logger;
    }

    /**
     * Adds a test logger and other dependencies to a smart reactor release step
     * for testing.
     * 
     * @param step
     *            not null.
     * @param rtr
     *            not null.
     * @param releasePhases
     *            can be null, which is coerced to an empty list.
     * @param rollbackPhases
     *            can be null, which is coerced to an empty list.
     * @param availablePhases
     *            can be null.
     * @param releaseDescriptor
     *            can be null.
     * @param releaseEnvironment
     *            can be null.
     * @return never null.
     */
    public static TestLogger addLoggerAndReleaseDependencies(
	    final AbstractSmartReactorReleaseStep step, final RTR rtr,
	    final List<String> releasePhases,
	    final List<String> rollbackPhases,
	    final Map<String, ReleasePhase> availablePhases,
	    final ReleaseDescriptor releaseDescriptor,
	    final ReleaseEnvironment releaseEnvironment) {
	Validate.notNull(step, "step is null");
	Validate.notNull(rtr, "rtr is null");
	final TestLogger logger = addLogger(step);
	Deencapsulation.setField(step, "rtr", rtr);
	Deencapsulation.setField(
		step,
		"releasePhases",
		ObjectUtils.defaultIfNull(releasePhases,
			Collections.emptyList()));
	Deencapsulation.setField(
		step,
		"rollbackPhases",
		ObjectUtils.defaultIfNull(rollbackPhases,
			Collections.emptyList()));
	Deencapsulation.setField(step, "availablePhases", availablePhases);
	Deencapsulation.setField(step, "releaseDescriptor", releaseDescriptor);
	Deencapsulation
		.setField(step, "releaseEnvironment", releaseEnvironment);
	return logger;
    }

    /** Uninstantiable. */
    private TestUtils() {
    }

}
