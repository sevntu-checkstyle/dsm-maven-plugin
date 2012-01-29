package com.sevntu.maven.plugin.dsm;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * Initialising DSM plugin.
 * 
 * @goal dsm
 * @phase site
 */
public class DsmMavenReport extends AbstractMavenReport {

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Specifies the directory where the report will be generated. Path to your "target/classes" dir
	 * 
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @required
	 */
	private File buildOutputDirectory;

	/**
	 * Directory containing the generated project sites and report
	 * distributions.
	 * 
	 * @parameter alias="outputDirectory"
	 *            expression="${project.reporting.outputDirectory}"
	 * @required
	 */
	private File inputDirectory;

	/**
	 * Folder name of DSM report
	 */
	private String dsmDirectory = "dsm";

	@Override
	protected MavenProject getProject() {
		return project;
	}

	@Override
	protected Renderer getSiteRenderer() {
		throw new IllegalStateException(
				"This method is not expected to be called");
	}

	@Override
	protected String getOutputDirectory() {
		return buildOutputDirectory.getAbsolutePath();
	}

	@Override
	public String getOutputName() {
		return dsmDirectory + File.separator + "index";
	}

	@Override
	public String getName(Locale locale) {
		return getBundle(locale).getString("report.dsm-report.name");
	}

	@Override
	public String getDescription(Locale locale) {
		return getBundle(locale).getString("report.dsm-report.description");
	}

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		DsmReport dsmReport = new DsmReport();

		dsmReport.setOutputDirectory(getOutputDirectory());
		dsmReport.setDsmReportSiteDirectory(inputDirectory.getAbsolutePath() + File.separator
				+ dsmDirectory);

		dsmReport.startReport();
	}

	/**
	 * @return Return true for create the report without using Doxia
	 */
	@Override
	public boolean isExternalReport() {
		return true;
	}

	/**
	 * Gets the resource bundle for the specified locale.
	 * 
	 * @param locale
	 *            The locale of the currently generated report.
	 * @return The resource bundle for the requested locale.
	 */
	private ResourceBundle getBundle(Locale locale) {
		return ResourceBundle.getBundle("dsm-report", locale, getClass()
				.getClassLoader());
	}
}
