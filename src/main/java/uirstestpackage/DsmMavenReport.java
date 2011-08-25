package uirstestpackage;

import java.util.Locale;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

/**
 * @goal dsm
 * @phase site
 */
public class DsmMavenReport extends AbstractMavenReport {

	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	protected MavenProject project;

	public static void main(String[] arg) {
		new DsmMavenReport().getOutputName();
	}

	@Override
	protected MavenProject getProject() {
		return project;
	}

	// Not used by Maven site plugin but required by API!
	@Override
	protected Renderer getSiteRenderer() {
		return null; // Nobody calls this!
	}

	// Not used by Maven site plugin but required by API!
	// (The site plugin is only calling getOutputName(), the output dir is
	// fixed!)
	@Override
	protected String getOutputDirectory() {
		return null; // Nobody calls this!
	}

	// Abused by Maven site plugin, a '/' denotes a directory path!
	public String getOutputName() {
		String path = "DSM";
		String outputFilename = "index";
		return path + "/" + outputFilename;
	}

	public String getName(Locale locale) {
		return "DSM";
	}

	public String getDescription(Locale locale) {
		new Main(null);
		return "A description of whatever MyReport generates.";
	}

	@Override
	protected void executeReport(Locale locale) throws MavenReportException {
	}

}
