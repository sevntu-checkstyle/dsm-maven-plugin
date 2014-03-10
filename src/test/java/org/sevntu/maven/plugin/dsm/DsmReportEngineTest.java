package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import junit.framework.Assert;

import org.junit.Test;


public class DsmReportEngineTest {

	@Test
	public void setDsmReportSiteDirectoryTest() {
		Exception ex = null;
		DsmReportEngine dsmReport = new DsmReportEngine();
		try {
			dsmReport.setOutputDirectory(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Dsm directory is empty.", e.getMessage());
		}
		assertNotNull(ex);
	}


	@Test
	public void setSourceDirectoryTest() {
		Exception ex = null;
		DsmReportEngine dsmReport = new DsmReportEngine();
		try {
			dsmReport.setSourceDirectory(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Source directory path can't be empty.", e.getMessage());
		}

		try {
			dsmReport.setSourceDirectory("");
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Source directory path can't be empty.", e.getMessage());
		}

		try {
			dsmReport.setSourceDirectory("/not/exists/directory");
			Assert.fail();
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Source directory '/not/exists/directory' not exists", e.getMessage());
		}

		assertNotNull(ex);
	}
}
