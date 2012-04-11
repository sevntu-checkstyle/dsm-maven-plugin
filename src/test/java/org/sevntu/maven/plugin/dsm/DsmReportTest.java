package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;


public class DsmReportTest {

	@Test
	public void setDsmReportSiteDirectoryTest() {
		Exception ex = null;
		DsmReport dsmReport = new DsmReport();
		try {
			dsmReport.setOutputDirectory(null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Dsm directory has no path.", e.getMessage());
		}
		assertNotNull(ex);
	}


	@Test
	public void setSourceDirectoryTest() {
		Exception ex = null;
		DsmReport dsmReport = new DsmReport();
		try {
			dsmReport.setSourceDirectory(null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Source directory has no path.", e.getMessage());
		}
		assertNotNull(ex);
	}
}
