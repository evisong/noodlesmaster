package me.evis.mobile.noodle.test;

import junit.framework.TestCase;
import me.evis.mobile.util.DataUtil;

public class DataUtilTest extends TestCase {

	/**
	 * Note: test project and target project should be both built before 
	 * running the test case. Or there will be class/method not found 
	 * exceptions.
	 */
	public void testGenerateUuid() {
		System.out.println(DataUtil.generateUuid());
	}

}
