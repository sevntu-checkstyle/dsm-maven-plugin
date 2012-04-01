package org.sevntu.maven.plugin.dsm;

import java.io.File;
import java.util.Locale;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.codehaus.plexus.PlexusTestCase;
import org.junit.Test;


public class DsmReportMojoTest extends AbstractMojoTestCase {

	@Test
	public void testDsmReportMojo() throws Exception {
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


	@Test
	public void testGetOutputName() throws Exception {
		String outputDirectory = "dsm";
		DsmReportMojo dsmReportMojo = new DsmReportMojo();
		dsmReportMojo.setDsmDirectory(outputDirectory);
		org.junit.Assert.assertEquals(outputDirectory + File.separator + "index",
				dsmReportMojo.getOutputName());
	}


	@Test
	public void testGetDescription() {
		Exception ex = null;
		String description = null;
		DsmReportMojo dsmReportMojo = new DsmReportMojo();
		try {
			description = dsmReportMojo.getDescription(null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("locale should not be null", e.getMessage());
		}
		assertNotNull(ex);

		description = dsmReportMojo.getDescription(Locale.ENGLISH);
		assertNotNull(description);
	}


	@Test
	public void testGetName() {
		Exception ex = null;
		String name = null;
		DsmReportMojo dsmReportMojo = new DsmReportMojo();
		try {
			name = dsmReportMojo.getName(null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("locale should not be null", e.getMessage());
		}
		assertNotNull(ex);

		name = dsmReportMojo.getName(Locale.ENGLISH);
		assertNotNull(name);
	}


	@Test
	public void testGetSourseDir() {
		MavenProject mavenProject = new MavenProject();
		mavenProject.setBasedir(new File("basedir"));

		DsmReportMojo dsmReportMojo = new DsmReportMojo();
		dsmReportMojo.setProject(mavenProject);
		dsmReportMojo.setDataFile(new File("datadir"));

		String sourceDir = dsmReportMojo.getSourseDir();
		assertEquals(System.getProperty("user.dir") + "/datadir", sourceDir);

		dsmReportMojo.setDataFile(null);
		sourceDir = dsmReportMojo.getSourseDir();
		assertEquals(System.getProperty("user.dir") + "/basedir", sourceDir);
	}


	@Test
	public void testGetProject() {
		MavenProject mavenProject = new MavenProject();
		DsmReportMojo dsmReportMojo = new DsmReportMojo();
		dsmReportMojo.setProject(mavenProject);
		assertEquals(mavenProject, dsmReportMojo.getProject());
	}
}
