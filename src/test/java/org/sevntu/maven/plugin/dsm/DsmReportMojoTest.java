package org.sevntu.maven.plugin.dsm;

import java.io.File;
import java.util.Locale;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.reporting.MavenReport;
import org.codehaus.plexus.PlexusTestCase;
import org.junit.Test;


public class DsmReportMojoTest extends AbstractMojoTestCase {

	@Test
	public void testGetDescription() {
		String description = null;
		DsmReportMojo dsmReportMojo = new DsmReportMojo();

		description = dsmReportMojo.getDescription(Locale.ENGLISH);
		assertNotNull(description);
	}


	@Test
	public void testGetName() {
		String name = null;
		DsmReportMojo dsmReportMojo = new DsmReportMojo();

		name = dsmReportMojo.getName(Locale.ENGLISH);
		assertNotNull(name);
	}


	@Test
	public void testGetSiteRenderer() {
		DsmReportMojo dsmReportMojo = new DsmReportMojo();
		assertNull(dsmReportMojo.getSiteRenderer());
	}


	@Test
	public void testGetProject() {
		DsmReportMojo dsmReportMojo = new DsmReportMojo();
		assertNull(dsmReportMojo.getProject());
	}


	@Test
	public void testDsmReportMojo() throws Exception {
		Mojo mojo = lookupMojo("dsm", PlexusTestCase.getBasedir()
				+ "/src/test/resources/report-plugin-config.xml");

		MavenReport reportMojo = (MavenReport) mojo;

		assertTrue("Should be able to generate a report", reportMojo.canGenerateReport());

		assertTrue("Should be an externale report", reportMojo.isExternalReport());

		mojo.execute();

		File outputHtml = new File(reportMojo.getReportOutputDirectory() + File.separator
				+ "index.html");
		assertTrue("Test for generated html file " + outputHtml, outputHtml.exists());
	}

}
