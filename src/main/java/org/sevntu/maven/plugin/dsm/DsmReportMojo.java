package org.sevntu.maven.plugin.dsm;

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
public class DsmReportMojo extends AbstractMavenReport {

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The output directory for the report.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * The Datafile Location.
	 * 
	 * @required
	 * @readonly
	 */
	private File dataFile;

	/**
	 * <i>Maven Internal</i>: The Doxia Site Renderer.
	 * 
	 * @component
	 */
	private Renderer siteRenderer;

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
		return siteRenderer;
	}


	@Override
	public String getOutputName() {
		return dsmDirectory + File.separator + "index";
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

		dsmReport.setSourceDirectory(getSourseDir());
		dsmReport.setDsmReportSiteDirectory(getOutputDirectory());

		dsmReport.startReport();
	}


	private String getSourseDir() {
		if (dataFile != null) {
			return dataFile.getAbsolutePath();
		}
		return project.getBasedir().getAbsolutePath();
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


	@Override
	protected String getOutputDirectory() {
		return outputDirectory.getAbsolutePath() + File.separator + dsmDirectory;
	}
}
