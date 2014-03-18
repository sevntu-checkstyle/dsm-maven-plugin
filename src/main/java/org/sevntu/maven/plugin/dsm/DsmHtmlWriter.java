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
import org.dtangler.javaengine.types.JavaScope;

import com.google.common.base.Strings;

import freemarker.template.Configuration;
import freemarker.template.Template;


/**
 * Generate site content and write to HTML file.
 * 
 * @author Yuri Balakhonov
 * @author Ilja Dubinin
 */
public class DsmHtmlWriter {

	public final static String FILE_FORMAT = ".html";
	public final static String IMAGE_FOLDER_NAME = "images";
	public final static String CSS_FOLDER_NAME = "css";
	public final static String FTL_CLASSES_PAGE = "classes_page.ftl";
	public final static String FTL_PACKAGES_PAGE = "packages_page.ftl";
	public final static String FTL_PACKAGES_PAGE_TRUNC = "packages_page_truncated.ftl";
	public final static String FTL_PACKAGES_MENU = "packages_menu.ftl";

	/**
	 * Path to your site report dir
	 */
	private final String reportSiteDirectory;

	/**
	 * Obfuscate package names.
	 */
	private final boolean obfuscatePackageNames;


	/**
	 * @param aReportSiteDirectory
	 */
	public DsmHtmlWriter(String aReportSiteDirectory, boolean obfuscate) {
		if (Strings.isNullOrEmpty(aReportSiteDirectory)) {
			throw new IllegalArgumentException(
					"Path to the report directory should not be null or empty");
		}
		this.obfuscatePackageNames = obfuscate;
		reportSiteDirectory = aReportSiteDirectory;

		new File(reportSiteDirectory).mkdirs();
	}


	/**
	 * @param aDataModel
	 * @param aTemplateName
	 * @return
	 * @throws Exception
	 */
	private static ByteArrayOutputStream renderTemplate(Map<String, Object> aDataModel,
			String aTemplateName) throws Exception {

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

		writeModelToFile("packages", FTL_PACKAGES_MENU, dataModel);

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
	 * @param aTitle
	 *            Name of package
	 */
	public void printDsm(final Dsm aDsm, final AnalysisResult aAnalysisResult, final Scope scope,
			final String aTitle, final String templateName) throws Exception {
		if (aDsm == null) {
			throw new IllegalArgumentException("DSM structure should not be null");
		}
		if (aAnalysisResult == null) {
			throw new IllegalArgumentException("Analysis structure should not be null");
		}
		if (scope == null) {
			throw new IllegalArgumentException("Scope should not be null");
		}
		if (Strings.isNullOrEmpty(aTitle)) {
			throw new IllegalArgumentException("Title of DSM should not be empty");
		}
		if (Strings.isNullOrEmpty(templateName)) {
			throw new IllegalArgumentException("Template name should not be empty");
		}

		List<DsmRowModel> dsmRowsData = new ArrayList<DsmRowModel>();
		List<String> names = new ArrayList<String>();
		int[] numberOfClassesInPackage = new int[aDsm.getRows().size()];

		for (int packageIndex = 0; packageIndex < aDsm.getRows().size(); packageIndex++) {
			DsmRow dsmRow = aDsm.getRows().get(packageIndex);

			String packageName = dsmRow.getDependee().getDisplayName();
			names.add(packageName);
			numberOfClassesInPackage[packageIndex] = dsmRow.getDependee().getContentCount();

			String truncatedPackageName = truncatePackageName(packageName, 20);
			List<String> dependenciesNumbers = new ArrayList<String>();
			for (DsmCell dep : dsmRow.getCells()) {
				dependenciesNumbers.add(formatDependency(dep, aAnalysisResult, scope));
			}

			DsmRowModel rowData = new DsmRowModel(packageIndex + 1, packageName,
					truncatedPackageName, dependenciesNumbers);
			dsmRowsData.add(rowData);
		}

		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("title", aTitle);
		dataModel.put("rows", dsmRowsData);
		dataModel.put("names", names);
		dataModel.put("numberOfClasses", numberOfClassesInPackage);
		writeModelToFile(aTitle, templateName, dataModel);
	}


	/**
	 * Write model to output file.
	 * 
	 * @param aFileName
	 * @param aTemplateName
	 * @param aDataModel
	 * @throws Exception
	 */
	private void writeModelToFile(String aFileName, String aTemplateName,
			Map<String, Object> aDataModel) throws Exception {
		ByteArrayOutputStream outputStream = renderTemplate(aDataModel, aTemplateName);
		writeStreamToFile(outputStream, aFileName);
		outputStream.close();
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
	private static String formatDependency(final DsmCell aDsmCell,
			final AnalysisResult aAnalysisResult, final Scope scope) {
		String dependencyType;
		if (!aDsmCell.isValid()) {
			dependencyType = "x";
		} else if (aDsmCell.getDependencyWeight() == 0) {
			dependencyType = "";
		} else {
			dependencyType = Integer.toString(aDsmCell.getDependencyWeight());
			if (!aAnalysisResult.getViolations(aDsmCell.getDependency(), Severity.error).isEmpty()) {
				dependencyType = dependencyType + "C";
			}

			if (!JavaScope.classes.equals(scope)) {
				Dependency dep = aDsmCell.getDependency();
				String dependantName = dep.getDependant().getDisplayName();
				String ependeeName = dep.getDependee().getDisplayName();
				String dsmName = dependantName + "-" + ependeeName;

				dependencyType = String
						.format("<a href='%s.html' >%s</a>", dsmName, dependencyType);
			}
		}
		return dependencyType;
	}


	private String truncatePackageName(String name, int length) {
		String truncatedName = name;
		if (obfuscatePackageNames) {
			String[] nameTokens = name.split("\\.");
			for (int numberOfToken = 0; numberOfToken < nameTokens.length
					&& truncatedName.length() > length; numberOfToken++) {
				String currentToken = nameTokens[numberOfToken];
				truncatedName = truncatedName.replace(currentToken + ".",
						currentToken.substring(0, 1) + ".");
			}
		} else {
			if (name.length() - 2 > length) {
				truncatedName = ".." + name.substring(name.length() - length - 2);
			}
		}
		return truncatedName;
	}

}
