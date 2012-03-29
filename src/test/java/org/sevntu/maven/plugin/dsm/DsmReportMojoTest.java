package org.sevntu.maven.plugin.dsm;

import java.io.File;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.reporting.MavenReport;
import org.codehaus.plexus.PlexusTestCase;


public class DsmReportMojoTest extends AbstractMojoTestCase {

	public void testReport() throws Exception {
		Mojo mojo = lookupMojo("dsm", PlexusTestCase.getBasedir()
				+ "/src/test/resources/report-plugin-config.xml");

		MavenReport reportMojo = (MavenReport) mojo;

		assertTrue("Should be able to generate a report", reportMojo.canGenerateReport());

		assertTrue("Should be an externale report", reportMojo.isExternalReport());

		mojo.execute();

		File outputHtml = new File(reportMojo.getReportOutputDirectory().getParent(),
				reportMojo.getOutputName() + ".html");

		assertTrue("Test for generated html file " + outputHtml, outputHtml.exists());
	}
}
