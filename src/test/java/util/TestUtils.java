package util;

import info.ronjenkins.maven.rtr.steps.SmartReactorStep;
import mockit.Deencapsulation;
import mockit.Mock;
import mockit.MockUp;

import org.apache.commons.lang.Validate;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

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
	Deencapsulation.setField(step, logger);
	return logger;
    }

    /**
     * Returns a new MavenProject object whose GAV matches the given parameters.
     * The scope is "compile", the type/packaging is "jar" and the classifier is
     * blank.
     * 
     * <p>
     * The underlying {@link Model} and {@link ArtifactHandler} are mocked, but
     * the underlying {@link Artifact} is not.
     * 
     * @param groupId
     *            not null.
     * @param artifactId
     *            not null.
     * @param version
     *            not null.
     * @return never null.
     */
    public static MavenProject mockMavenProject(final String groupId,
	    final String artifactId, final String version) {
	return mockMavenProject(groupId, artifactId, version, "compile", "jar",
		"");
    }

    /**
     * Returns a new MavenProject object whose scope and coordinate matches the
     * given parameters.
     * 
     * <p>
     * The underlying {@link Model} and {@link ArtifactHandler} are mocked, but
     * the underlying {@link Artifact} is not.
     * 
     * @param groupId
     *            not null.
     * @param artifactId
     *            not null.
     * @param version
     *            not null.
     * @param scope
     *            not null.
     * @param type
     *            not null.
     * @param classifier
     *            not null.
     * @return never null.
     */
    public static MavenProject mockMavenProject(final String groupId,
	    final String artifactId, final String version, final String scope,
	    final String type, final String classifier) {
	Validate.notNull(groupId, "groupId is null");
	Validate.notNull(artifactId, "artifactId is null");
	Validate.notNull(version, "version is null");
	Validate.notNull(scope, "scope is null");
	Validate.notNull(type, "type is null");
	Validate.notNull(classifier, "classifier is null");
	final Model model = new MockUp<Model>() {
	    @Mock
	    String getGroupId() {
		return groupId;
	    }

	    @Mock
	    String getArtifactId() {
		return artifactId;
	    }

	    @Mock
	    String getVersion() {
		return version;
	    }

	    @Mock
	    String getPackaging() {
		return type;
	    }

	    @Mock
	    public Model clone() {
		return this.getMockInstance();
	    }
	}.getMockInstance();
	final ArtifactHandler ah = new MockUp<ArtifactHandler>() {
	    @Mock
	    String getExtension() {
		return type;
	    }

	    @Mock
	    String getDirectory() {
		return "";
	    }

	    @Mock
	    String getClassifier() {
		return classifier;
	    }

	    @Mock
	    String getPackaging() {
		return type;
	    }
	}.getMockInstance();
	final Artifact artifact = new DefaultArtifact(groupId, artifactId,
		version, scope, type, classifier, ah);
	final MavenProject project = new MavenProject(model);
	project.setArtifact(artifact);
	return project;
    }

    /** Uninstantiable. */
    private TestUtils() {
    }

}
