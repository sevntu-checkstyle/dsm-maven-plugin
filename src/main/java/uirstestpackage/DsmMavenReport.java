package uirstestpackage;

import java.io.File;
import java.util.Locale;

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
	 * Specifies the directory where the report will be generated
	 * 
	 * @parameter default-value="${project.build.directory}"
	 * @required
	 */
	private File outputDirectory;
	private String siteFolderName = "DSM";

	@Override
	protected MavenProject getProject() {
		if (project == null) {
			throw new IllegalStateException("This value can not be null");
		}
		return project;
	}

	@Override
	protected Renderer getSiteRenderer() {
		throw new IllegalStateException("This method is not expected to be called");
	}

	@Override
	protected String getOutputDirectory() {
		return outputDirectory.getAbsolutePath();
	}

	@Override
	public String getOutputName() {
		System.setProperty("project.build.directory", getOutputDirectory());
		String outputFilename = "index";
		return siteFolderName + "/" + outputFilename;
	}

	@Override
	public String getName(Locale locale) {
		return siteFolderName;
	}

	@Override
	public String getDescription(Locale locale) {
		return "A description of whatever MyReport generates.";
	}

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
		new Main(null);
	}

}
