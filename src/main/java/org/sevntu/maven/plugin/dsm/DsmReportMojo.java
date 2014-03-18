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
 * 
 * @author Yuri Balakhonov
 * @author Ilja Dubinin
 */
public class DsmReportMojo extends AbstractMavenReport {

	/**
	 * Folder name of DSM report
	 */
	private final String dsmDirectory = "dsm";

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
	 * Obfuscate package names.
	 * 
	 * @parameter expression="${obfuscatePackageNames}" default-value="false"
	 */
	private boolean obfuscatePackageNames;

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private Renderer siteRenderer;


	public void setObfuscatePackageNames(boolean aObfuscate) {
		this.obfuscatePackageNames = aObfuscate;
	}


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

		dsmReport.setObfuscatePackageNames(obfuscatePackageNames);
		dsmReport.setSourceDirectory(getSourseDir());
		dsmReport.setOutputDirectory(getOutputDirectory());

		try {
			dsmReport.report();
		} catch (Exception e) {
			throw new MavenReportException("Error in DSM Report generation.", e);
		}
	}


	@Override
	public boolean isExternalReport() {
		return true;
	}

}
