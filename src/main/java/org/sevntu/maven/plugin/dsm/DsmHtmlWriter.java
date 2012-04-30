package org.sevntu.maven.plugin.dsm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation.Severity;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * Generate site content and write to HTML file.
 * 
 * @author yuriy
 * 
 */
public class DsmHtmlWriter {

	public final static String FILE_FORMAT = ".html";
	public final static String IMAGE_FOLDER_NAME = "images";
	public final static String CSS_FOLDER_NAME = "css";
	public final static String FTL_CLASSES_PAGE = "classes_page.ftl";
	public final static String FTL_PACKAGES_PAGE = "packages_page.ftl";
	public final static String FTL_PACKAGES_MENU = "packages_menu.ftl";

	/**
	 * Path to your site report dir
	 */
	private final String reportSiteDirectory;


	/**
	 * 
	 * @param aReportSiteDirectory
	 */
	public DsmHtmlWriter(String aReportSiteDirectory) {
		if (aReportSiteDirectory == null || DsmHtmlWriter.isEmptyString(aReportSiteDirectory)) {
			throw new IllegalArgumentException(
					"Path to the report directory should not be null or empty");
		}
		reportSiteDirectory = aReportSiteDirectory;

		new File(reportSiteDirectory).mkdirs();
	}


	/**
	 * 
	 * @param aDataModel
	 * @param aTemplateName
	 * @return
	 * @throws Exception
	 */
	public ByteArrayOutputStream processTemplate(Map<String, Object> aDataModel,
			String aTemplateName) throws Exception {
		ByteArrayOutputStream baos;

		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(DsmHtmlWriter.class, File.separator + "templates");

		baos = new ByteArrayOutputStream();
		Writer out = new OutputStreamWriter(baos);

		Template tpl = cfg.getTemplate(aTemplateName);
		tpl.process(aDataModel, out);
		return baos;
	}


	/**
	 * 
	 * @param baos
	 * @param aFileName
	 * @throws Exception
	 */
	private void writeStreamToFile(ByteArrayOutputStream baos, String aFileName) throws Exception {
		String filePath = reportSiteDirectory + File.separator + aFileName + FILE_FORMAT;

		OutputStream outputStream = new FileOutputStream(filePath);
		outputStream.write(baos.toByteArray());
	}


	/**
	 * Print navigation on site by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	public void printNavigateDsmPackages(final List<String> aPackageNames) throws Exception {
		if (aPackageNames == null) {
			throw new IllegalArgumentException("List of package names should not be null");
		}

		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("aPackageNames", aPackageNames);

		ByteArrayOutputStream baos = processTemplate(dataModel, FTL_PACKAGES_MENU);
		writeStreamToFile(baos, "packages");
	}


	/**
	 * Print dependency structure matrix
	 * 
	 * @param aDsm
	 *            Dsm structure
	 * @param aAnalysisResult
	 *            Analysis structure
	 * @param aName
	 *            Name of package
	 */
	public void printDsm(final Dsm aDsm, final AnalysisResult aAnalysisResult, final String aName,
			final String templateName) throws Exception {
		if (aDsm == null) {
			throw new IllegalArgumentException("DSM structure should not be null");
		}
		if (aAnalysisResult == null) {
			throw new IllegalArgumentException("Analysis structure should not be null");
		}
		if (isEmptyString(aName)) {
			throw new IllegalArgumentException("Title of DSM should not be empty");
		}

		List<Integer> headerIndexes = new ArrayList<Integer>();
		List<DsmRowData> dsmRowDatas = new ArrayList<DsmRowData>();

		int packageIndex = 1;
		for (DsmRow dsmRow : aDsm.getRows()) {

			headerIndexes.add(packageIndex);

			String packageName = formatName(dsmRow.getDependee().getDisplayName(), 40);
			int dependencyContentCount = dsmRow.getDependee().getContentCount();

			List<String> dependenciesNumbers = new ArrayList<String>();
			for (DsmCell dep : dsmRow.getCells()) {
				dependenciesNumbers.add(formatDependency(dep, aAnalysisResult));
			}

			DsmRowData rowData = new DsmRowData(packageIndex, packageName, dependencyContentCount,
					dependenciesNumbers);
			dsmRowDatas.add(rowData);

			packageIndex++;
		}

		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("title", aName);
		dataModel.put("headerIndexes", headerIndexes);
		dataModel.put("rows", dsmRowDatas);

		ByteArrayOutputStream baos = processTemplate(dataModel, templateName);
		writeStreamToFile(baos, aName);
	}


	/**
	 * Analyzing dependency
	 * 
	 * @param aDep
	 *            Count of dependency (number, 'x', or cycle)
	 * @param aAnalysisResult
	 *            Analysis structure
	 * @return Count of dependency
	 */
	private String formatDependency(final DsmCell aDep, final AnalysisResult aAnalysisResult) {
		String s;
		if (!aDep.isValid()) {
			s = "x";
		} else if (aDep.getDependencyWeight() == 0) {
			s = "";
		} else {
			s = Integer.toString(aDep.getDependencyWeight());
			if (!aAnalysisResult.getViolations(aDep.getDependency(), Severity.error).isEmpty()) {
				s = s + "C";
			}
		}
		return s;
	}


	/**
	 * Truncate package or class name
	 * 
	 * @param aName
	 *            Package or class name
	 * @param aLength
	 *            Maximum length of name
	 * @return Truncated name
	 */
	private String formatName(final String aName, final int aLength) {
		if (aName.length() - 2 <= aLength) {
			return aName;
		}
		return ".." + aName.substring(aName.length() - aLength - 2);
	}


	/**
	 * 
	 * @param aText
	 * @return
	 */
	public final static boolean isEmptyString(String aText) {
		return aText == null || aText.trim().isEmpty();
	}

}
