package org.sevntu.maven.plugin.dsm;

import junit.framework.TestCase;


public class DsmHtmlWriterTest extends TestCase {

	private DsmHtmlWriter dsmHtmlWriter;


	protected void setUp() {
		Exception ex = null;
		try {
			dsmHtmlWriter = new DsmHtmlWriter(null);
		} catch (Exception e) {
			ex = e;
			assertEquals("Path to the report directory should not be null or empty", e.getMessage());
		}
		assertNotNull(ex);
		dsmHtmlWriter = new DsmHtmlWriter(System.getProperty("user.dir"));
	}

	public void testFreemarkerDo() {
		//dsmHtmlWriter.
	}


	public void test3() {
		System.out.println("t3");
	}
}
