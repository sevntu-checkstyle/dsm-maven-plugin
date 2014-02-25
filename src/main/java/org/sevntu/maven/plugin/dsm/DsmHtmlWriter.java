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
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;

import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * Generate site content and write to HTML file.
 * 
 * @author yuriy
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
	 * @param aReportSiteDirectory
	 */
	public DsmHtmlWriter(String aReportSiteDirectory) {
		if (aReportSiteDirectory == null || DsmHtmlWriter.isNullOrEmpty(aReportSiteDirectory)) {
			throw new IllegalArgumentException(
					"Path to the report directory should not be null or empty");
		}
		reportSiteDirectory = aReportSiteDirectory;

		new File(reportSiteDirectory).mkdirs();
	}


	/**
	 * @param aDataModel
	 * @param aTemplateName
	 * @return
	 * @throws Exception
	 */
	public ByteArrayOutputStream renderTemplate(Map<String, Object> aDataModel, String aTemplateName)
			throws Exception {

		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(DsmHtmlWriter.class, File.separator + "templates");

		Template tpl = cfg.getTemplate(aTemplateName);

		ByteArrayOutputStream outputStrem = new ByteArrayOutputStream();
		Writer outputStreamWriter = new OutputStreamWriter(outputStrem);

		tpl.process(aDataModel, outputStreamWriter);
		return outputStrem;
	}


	/**
	 * @param byteOutputStream
	 * @param aFileName
	 * @throws Exception
	 */
	private void writeStreamToFile(ByteArrayOutputStream byteOutputStream, String aFileName)
			throws Exception {
		String filePath = reportSiteDirectory + File.separator + aFileName + FILE_FORMAT;

		OutputStream outputStream = new FileOutputStream(filePath);
		outputStream.write(byteOutputStream.toByteArray());
		outputStream.close();
	}


	/**
	 * Print navigation on site by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	public void printDsmPackagesNavigation(final List<String> aPackageNames) throws Exception {
		if (aPackageNames == null) {
			throw new IllegalArgumentException("List of package names should not be null");
		}

		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("aPackageNames", aPackageNames);

		ByteArrayOutputStream outputStream = renderTemplate(dataModel, FTL_PACKAGES_MENU);
		writeStreamToFile(outputStream, "packages");
		outputStream.close();
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
	public void printDsm(final Dsm aDsm, final AnalysisResult aAnalysisResult, Scope scope,
			final String aName, final String templateName) throws Exception {
		if (aDsm == null) {
			throw new IllegalArgumentException("DSM structure should not be null");
		}
		if (aAnalysisResult == null) {
			throw new IllegalArgumentException("Analysis structure should not be null");
		}
		if (isNullOrEmpty(aName)) {
			throw new IllegalArgumentException("Title of DSM should not be empty");
		}

		List<DsmRowModel> dsmRowsData = new ArrayList<DsmRowModel>();

		for (int packageIndex = 0; packageIndex < aDsm.getRows().size(); packageIndex++) {
			DsmRow dsmRow = aDsm.getRows().get(packageIndex);

			String packageName = dsmRow.getDependee().getDisplayName();
			int dependencyContentCount = dsmRow.getDependee().getContentCount();

			List<String> dependenciesNumbers = new ArrayList<String>();
			for (DsmCell dep : dsmRow.getCells()) {
				dependenciesNumbers.add(formatDependency(dep, aAnalysisResult, scope));
			}

			DsmRowModel rowData = new DsmRowModel(packageIndex + 1, packageName,
					dependencyContentCount, dependenciesNumbers);
			dsmRowsData.add(rowData);
		}

		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("title", aName);
		dataModel.put("rows", dsmRowsData);

		ByteArrayOutputStream outputStream = renderTemplate(dataModel, templateName);
		writeStreamToFile(outputStream, aName);
		outputStream.close();
	}


	/**
	 * Analyzing dependency
	 * 
	 * @param dsmCell
	 *            Count of dependency (number, 'x', or cycle)
	 * @param aAnalysisResult
	 *            Analysis structure
	 * @return Count of dependency
	 */
	private static String formatDependency(final DsmCell dsmCell,
			final AnalysisResult aAnalysisResult, final Scope scope) {
		String dependencyType;
		if (!dsmCell.isValid()) {
			dependencyType = "x";
		} else if (dsmCell.getDependencyWeight() == 0) {
			dependencyType = "";
		} else {
			dependencyType = Integer.toString(dsmCell.getDependencyWeight());
			if (!aAnalysisResult.getViolations(dsmCell.getDependency(), Severity.error).isEmpty()) {
				dependencyType = dependencyType + "C";
			}

			System.out.println("scope.index: " + scope.index());
			if (scope.index() != 2) {
				Dependency dep = dsmCell.getDependency();
				String dsmName = dep.getDependant().getDisplayName() + "-"
						+ dep.getDependee().getDisplayName();

				dependencyType = String
						.format("<a href='%s.html' >%s</a>", dsmName, dependencyType);
			}
		}
		return dependencyType;
	}


	/**
	 * @param aText
	 * @return
	 */
	public final static boolean isNullOrEmpty(String aText) {
		return aText == null || aText.trim().isEmpty();
	}

}
