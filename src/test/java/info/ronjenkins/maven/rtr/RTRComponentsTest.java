package info.ronjenkins.maven.rtr;

import static org.junit.Assert.*;
import mockit.Injectable;

import org.apache.maven.project.ProjectBuilder;
import org.junit.Test;

public final class RTRComponentsTest {

    @Injectable
    ProjectBuilder projectBuilder;

    @Test(expected = IllegalArgumentException.class)
    public void noNullProjectBuilder() {
	new RTRComponents(null);
    }

    @Test
    public void projectBuilder() {
	final RTRComponents rtrc = new RTRComponents(projectBuilder);
	assertEquals(projectBuilder, rtrc.getProjectBuilder());
    }

}
