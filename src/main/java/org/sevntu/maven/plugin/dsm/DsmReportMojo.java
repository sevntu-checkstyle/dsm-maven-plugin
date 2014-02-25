package org.sevntu.maven.plugin.dsm;

import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;


/**
 * Initializing DSM plugin.
 * 
 * @goal dsm
 * @phase site
 */
public class DsmReportMojo extends AbstractMavenReport {

	/**
	 * The output directory for the report.
	 * 
	 * @parameter default-value="${project.reporting.outputDirectory}"
	 * @required
	 */
	private File reportingOutputDirectory;

	/**
	 * Specifies the directory where the report will be generated. Path to your
	 * "target/classes" dir
	 * 
	 * @parameter default-value="${project.build.outputDirectory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Folder name of DSM report
	 */
	private String dsmDirectory = "dsm";


	@Override
	protected MavenProject getProject() {
		return null;
	}


	@Override
	protected Renderer getSiteRenderer() {
		return null;
	}


	@Override
	public String getOutputName() {
		return dsmDirectory + File.separator + "index";
	}


	@Override
	public String getName(final Locale aLocale) {
		return getBundle(aLocale).getString("report.dsm-report.name");
	}


	@Override
	public String getDescription(final Locale aLocale) {
		return getBundle(aLocale).getString("report.dsm-report.description");
	}


	@Override
	protected String getOutputDirectory() {
		return reportingOutputDirectory.getAbsolutePath() + File.separator + dsmDirectory;
	}


	/**
	 * @return project output directory
	 */
	private String getSourseDir() {
		return outputDirectory.getAbsolutePath();
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
	protected void executeReport(final Locale aLocale) throws MavenReportException {
		DsmReportEngine dsmReport = new DsmReportEngine();

		dsmReport.setSourceDirectory(getSourseDir());
		dsmReport.setOutputDirectory(getOutputDirectory());

		try {
			dsmReport.startReport();
		} catch (Exception e) {
			getLog().error("Error in Dsm Report generation: " + e.getMessage(), e);
		}
	}


	@Override
	public boolean isExternalReport() {
		return true;
	}

}
