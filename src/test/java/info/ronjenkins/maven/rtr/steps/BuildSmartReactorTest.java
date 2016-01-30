package info.ronjenkins.maven.rtr.steps;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Injectable;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Test;

public final class BuildSmartReactorTest {

    @Injectable
    MavenSession session;

    @Test
    public void onlySnapshotsRemain(@Injectable final MavenProject snapshot,
	    @Injectable final MavenProject release) {
	final BuildSmartReactor step = new BuildSmartReactor();
	final List<MavenProject> projects = new ArrayList<MavenProject>();
	projects.add(snapshot);
	projects.add(release);
	new Expectations() {
	    {
		session.getProjects();
		result = projects;
		snapshot.getArtifact().isSnapshot();
		result = true;
		release.getArtifact().isSnapshot();
		result = false;
	    }
	};
	step.execute(session, null);
	assertEquals(projects, session.getProjects());
	assertEquals(1, session.getProjects().size());
	assertEquals(snapshot, session.getProjects().get(0));
    }

}
