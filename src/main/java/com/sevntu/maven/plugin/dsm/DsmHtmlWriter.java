package com.sevntu.maven.plugin.dsm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import freemarker.template.TemplateException;


/**
 * Generate site content and write to HTML file.
 * 
 * @author yuriy
 * 
 */
public class DsmHtmlWriter {

	/**
	 * 
	 */
	private final String htmlFormat = ".html";

	/**
	 * Path to your site report dir
	 */
	private final String reportSiteDirectory;


	/**
	 * 
	 * @param aReportSiteDirectory
	 */
	public DsmHtmlWriter(String aReportSiteDirectory) {
		if (aReportSiteDirectory == null) {
			throw new IllegalArgumentException("Name of report directory should not be null");
		}
		reportSiteDirectory = aReportSiteDirectory;
	}


	/**
	 * 
	 * @param datamodel
	 * @param template
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	private String freemarkerDo(Map<String, Object> datamodel, String template, String aFileName)
			throws IOException, TemplateException {
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(DsmHtmlWriter.class, File.separator + "templates");
		Template tpl = cfg.getTemplate(template);

		String filePath = reportSiteDirectory + File.separator + aFileName + htmlFormat;
		Writer out = new FileWriter(filePath);

		tpl.process(datamodel, out);
		return out.toString();
	}


	/**
	 * Print navigation on site by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	public void printNavigateDsmPackages(final List<String> aPackageNames) {
		if (aPackageNames == null) {
			throw new IllegalArgumentException("List of package names should not be null");
		}

		Map<String, Object> datamodel = new HashMap<String, Object>();
		datamodel.put("aPackageNames", aPackageNames);

		StringBuilder htmlContent = new StringBuilder();
		try {
			htmlContent.append(freemarkerDo(datamodel, "packages_menu.html", "packages"));
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			// FIXME
		} catch (TemplateException e) {
			System.out.println(e.getLocalizedMessage());
			// FIXME
		}
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
			final String templateName) {
		if (aDsm == null) {
			throw new IllegalArgumentException("DSM structure should not be null");
		}
		if (aAnalysisResult == null) {
			throw new IllegalArgumentException("Analysis structure should not be null");
		}
		if (!isNotEmptyString(aName)) {
			throw new IllegalArgumentException("Title of DSM should not be empty");
		}

		ArrayList<Integer> headerIndexes = new ArrayList<Integer>();
		ArrayList<DsmRowData> dsmRowDatas = new ArrayList<DsmRowData>();

		int packageIndex = 1;
		for (DsmRow dsmRow : aDsm.getRows()) {

			headerIndexes.add(packageIndex);

			String packageName = formatName(dsmRow.getDependee().getDisplayName(), 40);
			int depCount = dsmRow.getDependee().getContentCount();

			ArrayList<String> dependenciesNumbers = new ArrayList<String>();
			for (DsmCell dep : dsmRow.getCells()) {
				dependenciesNumbers.add(formatDependency(dep, aAnalysisResult));
			}

			DsmRowData rowData = new DsmRowData(packageIndex, packageName, depCount,
					dependenciesNumbers);
			dsmRowDatas.add(rowData);

			packageIndex++;
		}

		Map<String, Object> datamodel = new HashMap<String, Object>();
		datamodel.put("title", aName);
		datamodel.put("headerIndexes", headerIndexes);
		datamodel.put("rows", dsmRowDatas);

		StringBuilder htmlContent = new StringBuilder();
		try {
			htmlContent.append(freemarkerDo(datamodel, templateName, aName));
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			// FIXME
		} catch (TemplateException e) {
			System.out.println(e.getLocalizedMessage());
			// FIXME
		}
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
		if (!aDep.isValid()) {
			return "x";
		}
		if (aDep.getDependencyWeight() == 0) {
			return "";
		}
		String s = Integer.toString(aDep.getDependencyWeight());
		if (!aAnalysisResult.getViolations(aDep.getDependency(), Severity.error).isEmpty()) {
			s = s + "C";
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
	public final static boolean isNotEmptyString(String aText) {
		return (aText != null) && (!aText.trim().isEmpty());
	}

	/**
	 * 
	 * @author yuriy
	 * 
	 */
	public class DsmRowData {

		private String name;
		private ArrayList<String> numberOfDependencies;
		private int depCount;
		private int positionIndex;


		public DsmRowData(int positionIndex, String name, int depCount,
				ArrayList<String> numberOfDependencies) {
			this.positionIndex = positionIndex;
			this.name = name;
			this.depCount = depCount;
			this.numberOfDependencies = numberOfDependencies;
		}


		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}


		/**
		 * @return the numberOfDependencies
		 */
		public ArrayList<String> getNumberOfDependencies() {
			return numberOfDependencies;
		}


		/**
		 * @return the depCount
		 */
		public int getDepCount() {
			return depCount;
		}


		/**
		 * @return the positionIndex
		 */
		public int getPositionIndex() {
			return positionIndex;
		}
	}
}
