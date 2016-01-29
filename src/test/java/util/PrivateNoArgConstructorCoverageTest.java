package util;

import info.ronjenkins.maven.rtr.RTRConfig;
import mockit.Deencapsulation;

import org.junit.Test;

public final class PrivateNoArgConstructorCoverageTest {

    @Test
    public void cover() {
	final Class<?>[] classes = new Class<?>[] { RTRConfig.class };
	for (final Class<?> clazz : classes) {
	    Deencapsulation.newInstance(clazz);
	}
    }

}
