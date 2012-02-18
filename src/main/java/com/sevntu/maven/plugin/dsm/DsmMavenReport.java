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
	 * Specifies the directory where the report will be generated. Path to your
	 * "target/classes" dir
	 * 
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @required
	 */
	private File buildOutputDirectory;

	private File reportOutputDirectory;

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
		throw new IllegalStateException("This method is not expected to be called");
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
	public File getReportOutputDirectory() {
		if (reportOutputDirectory == null) {
			reportOutputDirectory = new File(getOutputDirectory());
		}
		return reportOutputDirectory;
	}


	@Override
	public void setReportOutputDirectory(File reportOutputDirectory) {
		this.reportOutputDirectory = reportOutputDirectory;
	}


	@Override
	public String getName(final Locale aLocale) {
		if (aLocale == null) {
			throw new IllegalArgumentException("locale should not be null");
		}
		return getBundle(aLocale).getString("report.dsm-report.name");
	}


	@Override
	public String getDescription(final Locale aLocale) {
		if (aLocale == null) {
			throw new IllegalArgumentException("locale should not be null");
		}
		return getBundle(aLocale).getString("report.dsm-report.description");
	}


	@Override
	protected void executeReport(final Locale aLocale) throws MavenReportException {
		if (aLocale == null) {
			throw new IllegalArgumentException("locale should not be null");
		}

		DsmReport dsmReport = new DsmReport();
		dsmReport.setOutputDirectory(getOutputDirectory());
		dsmReport.setDsmReportSiteDirectory(reportOutputDirectory.getAbsolutePath()
				+ File.separator + dsmDirectory);

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
	 * @param aLocale
	 *            The locale of the currently generated report.
	 * @return The resource bundle for the requested locale.
	 */
	private ResourceBundle getBundle(final Locale aLocale) {
		return ResourceBundle.getBundle("dsm-report", aLocale, getClass().getClassLoader());
	}
}
