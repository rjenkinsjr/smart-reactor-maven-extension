package info.ronjenkins.maven.rtr;

import mockit.Mock;
import mockit.MockUp;

import org.apache.commons.lang.Validate;
import org.apache.maven.model.Model;
import org.apache.maven.project.MavenProject;

/**
 * Utility functions to facilitate testing.
 * 
 * @author Ronald Jack Jenkins Jr.
 */
public final class TestUtils {

    /**
     * Returns a new, real MavenProject object constructed from a mocked Model,
     * whose GAV matches the given parameters.
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
	Validate.notNull(groupId, "groupId is null");
	Validate.notNull(artifactId, "artifactId is null");
	Validate.notNull(version, "version is null");
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
	}.getMockInstance();
	return new MavenProject(model);
    }

    /** Uninstantiable. */
    private TestUtils() {
    }

}
