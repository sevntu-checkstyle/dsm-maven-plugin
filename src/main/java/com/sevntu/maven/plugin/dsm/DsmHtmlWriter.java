package com.sevntu.maven.plugin.dsm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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

	private final String siteHeaders = "SiteHeaders.html";
	private final String documentDSMHeader = siteHeaders;
	private final String htmlFormat = ".html";
	private final String linkTarget = "summary";
	private final String allPackagesFilePath = "." + File.separator + "all_packages.html";
	private final String packageIconPath = "." + File.separator + "images" + File.separator
			+ "package.png";
	private final String classIconpath = "." + File.separator + "images" + File.separator
			+ "class.png";

	/* Path to your site report dir */
	private final String reportSiteDirectory;

	private int nextRow = 0;


	public DsmHtmlWriter(String aReportSiteDirectory) {
		if (aReportSiteDirectory == null) {
			throw new IllegalArgumentException("Name of report directory should not be null");
		}
		reportSiteDirectory = aReportSiteDirectory;
	}


	public String freemarkerDo(Map<String, Object> datamodel, String template) throws IOException,
			TemplateException {
		Configuration cfg = new Configuration();
		cfg.setClassForTemplateLoading(DsmHtmlWriter.class, File.separator + "templates");
		Template tpl = cfg.getTemplate(template);
		Writer out = new StringWriter();
		tpl.process(datamodel, out);
		System.out.println(out.toString());
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
		datamodel.put("packages", aPackageNames);

		StringBuilder htmlContent = new StringBuilder();
		try {
			htmlContent.append(freemarkerDo(datamodel, "packages_menu.html"));
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			// FIXME
		} catch (TemplateException e) {
			e.printStackTrace();
			// FIXME
		}

		writeHtml("packages", htmlContent);
	}


	/**
	 * Get header content from resource file
	 * 
	 * @param aPath
	 *            Path to file with the headers content
	 * @return Headers content
	 */
	private String addHeaderContent(String aPath) {
		return getTemplate(aPath);
	}


	/**
	 * Print all packages
	 * 
	 * @param aDsm
	 *            Dsm structure
	 * @param aAnalysisResult
	 *            Analysis structure
	 * @param aAllPackagesName
	 *            Title of DSM
	 */
	public void printDsmPackages(final Dsm aDsm, final AnalysisResult aAnalysisResult,
			final String aAllPackagesName) {
		if (aDsm == null) {
			throw new IllegalArgumentException("DSM structure should not be null");
		}
		if (aAnalysisResult == null) {
			throw new IllegalArgumentException("Analysis structure should not be null");
		}
		if (!TagFactory.isNotEmptyString(aAllPackagesName)) {
			throw new IllegalArgumentException("Title of DSM should not be empty");
		}

		nextRow = 0;

		ArrayList<Integer> headerIndexes = new ArrayList<Integer>();
		ArrayList<DsmRowData> dsmRowDatas = new ArrayList<DsmRowData>();

		int packageIndex = 1;
		for (DsmRow dsmRow : aDsm.getRows()) {
			nextRow++;

			headerIndexes.add(packageIndex);

			String packageName = dsmRow.getDependee().getDisplayName();
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
		datamodel.put("aAllPackagesName", aAllPackagesName);
		datamodel.put("headerIndexes", headerIndexes);
		datamodel.put("packages", dsmRowDatas);

		StringBuilder htmlContent = new StringBuilder();
		try {
			htmlContent.append(freemarkerDo(datamodel, "packages_page.html"));
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			// FIXME
		} catch (TemplateException e) {
			e.printStackTrace();
			// FIXME
		}
		System.out.println("");

		// End table of DSM
		htmlContent.append(TagFactory.tableEnd());

		// Body end
		htmlContent.append(TagFactory.bodyEnd());

		// Write content to file and save it
		writeHtml(aAllPackagesName, htmlContent);
	}


	/**
	 * Print dependency structure matrix
	 * 
	 * @param aDsm
	 *            Dsm structure
	 * @param aAnalysisResult
	 *            Analysis structure
	 * @param aPackageName
	 *            Name of package
	 */
	public void printDsm(final Dsm aDsm, final AnalysisResult aAnalysisResult,
			final String aPackageName) {
		if (aDsm == null) {
			throw new IllegalArgumentException("DSM structure should not be null");
		}
		if (aAnalysisResult == null) {
			throw new IllegalArgumentException("Analysis structure should not be null");
		}
		if (!TagFactory.isNotEmptyString(aPackageName)) {
			throw new IllegalArgumentException("Title of DSM should not be empty");
		}

		nextRow = 0;
		StringBuilder htmlContent = new StringBuilder();

		htmlContent.append(addHeaderContent(documentDSMHeader));
		// Body start
		htmlContent.append(TagFactory.body(""));

		// Add DSM Title
		htmlContent.append(TagFactory.h1("")
				+ TagFactory.a("", allPackagesFilePath, "", linkTarget) + "DSM Report"
				+ TagFactory.aEnd() + " - " + TagFactory.img("", packageIconPath, "") + " "
				+ aPackageName + TagFactory.h1End());

		// Start table of DSM
		htmlContent.append(TagFactory.table(""));

		for (int i = 1; i <= aDsm.getRows().size(); i++) {
			printCell(Integer.toString(i), htmlContent);
		}

		int rowIndex = 1;
		for (DsmRow row : aDsm.getRows()) {
			nextRow++;
			printClasses(rowIndex++, row, aAnalysisResult, htmlContent);
		}

		// End table of DSM
		htmlContent.append(TagFactory.tableEnd());

		// Body end
		htmlContent.append(TagFactory.bodyEnd());

		// Write content to file and save it
		writeHtml(aPackageName, htmlContent);
	}


	/**
	 * Print class to DSM
	 * 
	 * @param aIndex
	 *            Index of printed class
	 * @param aRow
	 *            Dependency of class
	 * @param aAnalysisResult
	 *            Analysis structure
	 */
	private void printClasses(final int aIndex, final DsmRow aRow,
			final AnalysisResult aAnalysisResult, final StringBuilder aHtmlContent) {

		aHtmlContent.append(TagFactory.tr(""));

		printRowHeader(aIndex, aRow.getDependee().getDisplayName(), aRow.getDependee()
				.getContentCount(), false, aHtmlContent);

		for (DsmCell dep : aRow.getCells()) {
			printCell(formatDependency(dep, aAnalysisResult), aHtmlContent);
		}

		aHtmlContent.append(TagFactory.trEnd());
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
	 * Print row headers of matrix (icon with package name or class name)
	 * 
	 * @param aRowId
	 *            Row index
	 * @param aName
	 *            Package name or class name
	 * @param aPkgCount
	 *            Count of classes in package
	 * @param aIsPackages
	 *            Matrix of packages or classes
	 */
	private void printRowHeader(final int aRowId, final String aName, final int aPkgCount,
			final boolean aIsPackages, final StringBuilder aHtmlContent) {

		aHtmlContent.append(TagFactory.td("packageName_rows"));

		if (aIsPackages) {

			aHtmlContent.append(TagFactory.img("", packageIconPath, ""));

			aHtmlContent.append(TagFactory.a("", aName + htmlFormat, aName, "")
					+ formatName(aName, 40) + TagFactory.aEnd());
		} else {

			aHtmlContent.append(TagFactory.img("", classIconpath, ""));

			aHtmlContent.append(aName);
		}

		aHtmlContent.append(" (" + aPkgCount + ")");

		aHtmlContent.append(TagFactory.tdEnd());

		aHtmlContent.append(TagFactory.td("packageNumber_rows") + aRowId + TagFactory.tdEnd());
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
	 * Print cell content into the row
	 * 
	 * @param aCellContent
	 *            Cell content
	 */
	private void printCell(final String aCellContent, final StringBuilder aHtmlContent) {
		if (nextRow == 0) {
			aHtmlContent.append(TagFactory.td("packageName_cols") + aCellContent
					+ TagFactory.tdEnd());
		} else {
			aHtmlContent.append(TagFactory.td("") + aCellContent + TagFactory.tdEnd());
		}
	}


	/**
	 * Convert StringBuildr to String and write to file
	 * 
	 * @param aPackageName
	 *            Package file name
	 */
	private void writeHtml(final String aPackageName, final StringBuilder aHtmlContent) {
		String sHtmlContent = aHtmlContent.toString();
		String filePath = reportSiteDirectory + File.separator + aPackageName + htmlFormat;
		writeToFile(filePath, sHtmlContent);
	}


	private void writeToFile(final String aFilePath, final String aHtmlContent) {
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(aFilePath));
			bufferedWriter.write(aHtmlContent);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}


	/**
	 * 
	 * @param aPath
	 *            path to template file
	 * @return contents of the file
	 */
	private String getTemplate(final String aPath) {
		if (!TagFactory.isNotEmptyString(aPath)) {
			throw new IllegalArgumentException("Has no path to input file");
		}

		InputStream is = getClass().getResourceAsStream(File.separator + aPath);
		Writer writer = new StringWriter();
		char[] buffer = new char[1024];
		String fileContents = "";

		try {
			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			fileContents = writer.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileContents;
	}

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
