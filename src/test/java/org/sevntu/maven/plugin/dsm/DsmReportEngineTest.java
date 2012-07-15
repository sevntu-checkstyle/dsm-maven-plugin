package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class DsmReportEngineTest {

	@Test
	public void setDsmReportSiteDirectoryTest() {
		Exception ex = null;
		DsmReportEngine dsmReport = new DsmReportEngine();
		try {
			dsmReport.setOutputDirectory(null);
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
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Source directory is empty.", e.getMessage());
		}

		try {
			dsmReport.setSourceDirectory("/not/exists/directory");
		} catch (RuntimeException e) {
			ex = e;
			assertEquals("Source directory '/not/exists/directory' not exists", e.getMessage());
		}

		assertNotNull(ex);
	}
}
