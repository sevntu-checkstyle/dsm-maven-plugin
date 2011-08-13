package uirstestpackage;
import java.util.Locale;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
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
    // (The site plugin is only calling getOutputName(), the output dir is fixed!)
    @Override
    protected String getOutputDirectory() {
        return null; // Nobody calls this!
    }

    // Abused by Maven site plugin, a '/' denotes a directory path!
    public String getOutputName() {

        String path = "someDirectoryInTargetSite/canHaveSubdirectory/OrMore";
        String outputFilename = "some-file";

        // The site plugin will make the directory (and regognize the '/') in the path,
        // it will also append '.html' onto the filename and there is nothing (yes, I tried)
        // you can do about that. Feast your eyes on the code that instantiates 
        // this (good luck finding it):
        // org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext

        return path + "/" + outputFilename;
    }

    public String getName(Locale locale) {
        return "My Report";
    }

    public String getDescription(Locale locale) {
        return "A description of whatever MyReport generates.";
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {

        Sink sink = getSink();
        sink.head();
        sink.title();
        sink.text("DSM Report");
        sink.title_();
        sink.head_();

        sink.body();
        
        sink.rawText("Still nothing here!");

        sink.body_();
        sink.flush();
        sink.close();

    }
}
