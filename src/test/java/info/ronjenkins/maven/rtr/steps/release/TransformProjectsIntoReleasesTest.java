package info.ronjenkins.maven.rtr.steps.release;

import static org.junit.Assert.*;
import info.ronjenkins.maven.rtr.RTRComponents;
import info.ronjenkins.maven.rtr.RTRConfig;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Mocked;
import mockit.Verifications;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.release.config.ReleaseDescriptor;
import org.junit.Test;

public class TransformProjectsIntoReleasesTest {

    @Injectable
    MavenSession session;
    @Injectable
    MavenProject executionRoot;
    @Mocked
    RTRConfig config;

    @Test
    public void configureReleaseDescriptor() {
	final TransformProjectsIntoReleases tpir = new TransformProjectsIntoReleases();
	final ReleaseDescriptor rd = new ReleaseDescriptor();
	Deencapsulation.setField(tpir, "releaseDescriptor", rd);
	new Expectations() {
	    {
		RTRConfig.isAddSchema(session, executionRoot);
		result = true;
		RTRConfig.isAllowTimestampedSnapshots(session, executionRoot);
		result = true;
		RTRConfig.isAutoVersionSubmodules(session, executionRoot);
		result = true;
		RTRConfig.getProjectVersionPolicyId(session, executionRoot);
		result = "abc123";
		RTRConfig.getReleaseVersion(session, executionRoot);
		result = "rv";
		RTRConfig.getTag(session, executionRoot);
		result = "tag";
		RTRConfig.getTagBase(session, executionRoot);
		result = "tagBase";
		RTRConfig.getTagNameFormat(session, executionRoot);
		result = "tagNameFormat";
	    }
	};
	Deencapsulation.invoke(tpir, "configureReleaseDescriptor", session,
		RTRComponents.class);
	assertTrue(rd.isAddSchema());
	assertTrue(rd.isAllowTimestampedSnapshots());
	assertTrue(rd.isAutoVersionSubmodules());
	assertEquals("abc123", rd.getProjectVersionPolicyId());
	assertEquals("rv", rd.getDefaultReleaseVersion());
	assertEquals("tag", rd.getScmReleaseLabel());
	assertEquals("tagBase", rd.getScmTagBase());
	assertEquals("tagNameFormat", rd.getScmTagNameFormat());
    }

    @Test
    public void configureReleaseDescriptorWithoutOptionalParameters(
	    @Mocked final ReleaseDescriptor rd) {
	final TransformProjectsIntoReleases tpir = new TransformProjectsIntoReleases();
	Deencapsulation.setField(tpir, "releaseDescriptor", rd);
	new Expectations() {
	    {
		RTRConfig.getReleaseVersion(session, executionRoot);
		result = null;
		RTRConfig.getTag(session, executionRoot);
		result = null;
		RTRConfig.getTagBase(session, executionRoot);
		result = null;
		RTRConfig.getTagNameFormat(session, executionRoot);
		result = null;
	    }
	};
	Deencapsulation.invoke(tpir, "configureReleaseDescriptor", session,
		RTRComponents.class);
	new Verifications() {
	    {
		rd.setDefaultReleaseVersion(anyString);
		times = 0;
		rd.setScmReleaseLabel(anyString);
		times = 0;
		rd.setScmTagBase(anyString);
		times = 0;
		rd.setScmTagNameFormat(anyString);
		times = 0;
	    }
	};
    }

}
