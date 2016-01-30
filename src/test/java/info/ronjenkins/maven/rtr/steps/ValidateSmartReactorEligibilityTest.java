package info.ronjenkins.maven.rtr.steps;

import static org.junit.Assert.*;
import static util.TestUtils.*;
import info.ronjenkins.maven.rtr.exceptions.SmartReactorSanityCheckException;

import java.util.Arrays;

import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.execution.ProjectDependencyGraph;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

import util.TestLogger;

public final class ValidateSmartReactorEligibilityTest {

    @Injectable
    MavenSession session;
    @Mocked
    MavenProject root;

    @Test
    public void singleReleaseIsNotEligible() {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = addLogger(step);
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
    public void singleSnapshotIsEligible() {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = addLogger(step);
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

    @Test
    public void parentSnapshotAndChildSnapshotIsEligible(
	    @Injectable final ProjectDependencyGraph pdg,
	    @Injectable final MavenProject child) {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = addLogger(step);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		session.getProjects();
		result = Arrays.asList(root, child);
		session.getProjectDependencyGraph();
		result = pdg;
		root.getArtifact().isSnapshot();
		result = true;
		child.getArtifact().isSnapshot();
		result = true;
		pdg.getUpstreamProjects(child, true);
		result = root;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    fail(ExceptionUtils.getFullStackTrace(e));
	}
	assertTrue(logger.getErrorLog().isEmpty());
    }

    @Test
    public void parentSnapshotAndChildReleaseIsEligible(
	    @Injectable final MavenProject child) {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = addLogger(step);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		session.getProjects();
		result = Arrays.asList(root, child);
		root.getArtifact().isSnapshot();
		result = true;
		child.getArtifact().isSnapshot();
		result = false;
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    fail(ExceptionUtils.getFullStackTrace(e));
	}
	assertTrue(logger.getErrorLog().isEmpty());
    }

    @Test
    public void threeGenerationSnapshotFamilyIsEligible(
	    @Injectable final ProjectDependencyGraph pdg,
	    @Injectable final MavenProject child,
	    @Injectable final MavenProject grandchild) {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = addLogger(step);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		session.getProjects();
		result = Arrays.asList(root, child, grandchild);
		session.getProjectDependencyGraph();
		result = pdg;
		root.getArtifact().isSnapshot();
		result = true;
		child.getArtifact().isSnapshot();
		result = true;
		grandchild.getArtifact().isSnapshot();
		result = true;
		pdg.getUpstreamProjects(child, true);
		result = root;
		pdg.getUpstreamProjects(grandchild, true);
		result = Arrays.asList(child, root);
	    }
	};
	try {
	    step.execute(session, null);
	} catch (final MavenExecutionException e) {
	    fail(ExceptionUtils.getFullStackTrace(e));
	}
	assertTrue(logger.getErrorLog().isEmpty());
    }

    @Test
    public void threeGenerationSnapshotFamilyWithReleaseChildIsNotEligible(
	    @Injectable final ProjectDependencyGraph pdg,
	    @Injectable final MavenProject child,
	    @Injectable final MavenProject grandchild) {
	final ValidateSmartReactorEligibility step = new ValidateSmartReactorEligibility();
	final TestLogger logger = addLogger(step);
	new Expectations() {
	    {
		session.getTopLevelProject();
		result = root;
		session.getProjects();
		result = Arrays.asList(root, child, grandchild);
		session.getProjectDependencyGraph();
		result = pdg;
		root.getArtifact().isSnapshot();
		result = true;
		child.getArtifact().isSnapshot();
		result = false;
		grandchild.getArtifact().isSnapshot();
		result = true;
		pdg.getUpstreamProjects(grandchild, true);
		result = Arrays.asList(child, root);
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
