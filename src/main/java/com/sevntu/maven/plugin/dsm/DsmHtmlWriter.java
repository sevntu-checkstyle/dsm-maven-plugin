package com.sevntu.maven.plugin.dsm;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation.Severity;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;

/**
 * Generate site content and write to HTML file.
 * 
 * @author yuriy
 * 
 */
public class DsmHtmlWriter {

	private String siteHeaders = "SiteHeaders.html";
	private String documentPackagesHeader = "documentPackagesHeader.html";
	private String documentDSMHeader = "documentDSMHeader.html";
	private String htmlFormat = ".html";
	private String allPackagesFilePath = "./all_packages.html";
	private String packageIconPath = "./images/package.png";
	private String menuPackageIconPath = packageIconPath;
	private String packagesIconPath = packageIconPath;
	private String allPackageIconPath = packageIconPath;
	private String classIconpath = "./images/class.png";

	private TemplateEngine templ = new TemplateEngine();

	private StringBuilder htmlContent = new StringBuilder();

	private int nextRow = 0;

	/**
	 * Constructor
	 */
	public DsmHtmlWriter() {
	}

	/**
	 * Print navigation on site by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	public void printNavigateDsmPackages(List<String> aPackageNames) {
		htmlContent = new StringBuilder();
		/* Add headers */
		htmlContent.append(addHeaderContent(siteHeaders));
		/* Add Body */
		htmlContent.append(templ.body("") + templ.b("") + "Packages:"
				+ templ.b_() + templ.ul("") + templ.li("")
				+ templ.a("", allPackagesFilePath, "", "summary")
				+ templ.img("", packagesIconPath, "") + " All" + templ.a_()
				+ templ.li_());
		for (int index = 0; index < aPackageNames.size(); index++) {
			String packageName = aPackageNames.get(index);
			packageName = String.format(
					templ.li("") + templ.a("", "./%s.html", "", "summary")
							+ "%s %s" + templ.a_() + templ.li_(), packageName,
					templ.img("", menuPackageIconPath, ""), packageName);
			htmlContent.append(packageName);
		}
		htmlContent.append(templ.ul_() + templ.body_());
		writeHtml("packages");
	}

	/**
	 * Get header content from resource file
	 * 
	 * @param path
	 *            Path to file with the headers content
	 * @return Headers content
	 */
	private String addHeaderContent(String path) {
		ReadContentFromFile contentFromFile = new ReadContentFromFile(path);
		return contentFromFile.getTemplate();
	}

	/**
	 * Print all packages
	 * 
	 * @param dsm
	 *            Dsm structure
	 * @param analysisResult
	 *            Analysis structure
	 * @param aPackageName
	 *            Name of package
	 */
	public void printDsmPackages(Dsm dsm, AnalysisResult analysisResult,
			String aPackageName) {
		nextRow = 0;
		int i = 1;
		htmlContent = new StringBuilder();
		htmlContent.append(addHeaderContent(documentPackagesHeader));
		htmlContent.append(templ.body("") + templ.h1("")
				+ templ.a("", allPackagesFilePath, "", "summary")
				+ "DSM Report" + templ.a_() + " - "
				+ templ.img("", allPackageIconPath, "") + " " + aPackageName
				+ templ.h1_() + templ.table("") + templ.tr("") + templ.td(""));

		printColumnHeaders(dsm.getRows().size());

		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printPackage(i++, row, analysisResult);
		}

		htmlContent.append(templ.ul_() + templ.body_());
		writeHtml(aPackageName);
	}

	/**
	 * Print dependency structure matrix
	 * 
	 * @param dsm
	 *            Dsm structure
	 * @param analysisResult
	 *            Analysis structure
	 * @param aPackageName
	 *            Name of package
	 */
	public void printDsm(Dsm dsm, AnalysisResult analysisResult,
			String aPackageName) {
		int i = 1;
		nextRow = 0;
		htmlContent = new StringBuilder();
		htmlContent.append(addHeaderContent(documentDSMHeader));
		htmlContent.append(templ.body("") + templ.h1("")
				+ templ.a("", allPackagesFilePath, "", "summary")
				+ "DSM Report" + templ.a_() + " - "
				+ templ.img("", packageIconPath, "") + " " + aPackageName
				+ templ.h1_() + templ.table("") + templ.tr("") + templ.td(""));
		printColumnHeaders(dsm.getRows().size());
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printClasses(i++, row, analysisResult);
		}
		htmlContent.append(templ.table_() + templ.body_());
		writeHtml(aPackageName);
	}

	/**
	 * Print columns index
	 * 
	 * @param size
	 *            Count of columns
	 */
	private void printColumnHeaders(int size) {
		printEmptyMatrixTitle();
		for (int i = 1; i <= size; i++) {
			printCell(Integer.toString(i));
		}
		printEndRow();
	}

	/**
	 * Print class to DSM
	 * 
	 * @param index
	 *            Index of printed class
	 * @param row
	 *            Dependency of class
	 * @param analysisResult
	 *            Analysis structure
	 */
	private void printClasses(int index, DsmRow row,
			AnalysisResult analysisResult) {
		printRowHeader(index, row.getDependee().getDisplayName(), row
				.getDependee().getContentCount(), false);
		for (DsmCell dep : row.getCells()) {
			printCell(formatDependency(dep, analysisResult));
		}
		printEndRow();
	}

	/**
	 * Print package to DSM
	 * 
	 * @param index
	 *            Index of printed package
	 * @param row
	 *            Dependency of package
	 * @param analysisResult
	 *            Analysis structure
	 */
	private void printPackage(int index, DsmRow row,
			AnalysisResult analysisResult) {
		String packageName = row.getDependee().getDisplayName();

		printRowHeader(index, packageName, row.getDependee().getContentCount(),
				true);

		for (DsmCell dep : row.getCells()) {
			printCell(formatDependency(dep, analysisResult));
		}
		printEndRow();
	}

	/**
	 * Analyzing dependency
	 * 
	 * @param dep
	 *            Count of dependency (number, 'x', or cycle)
	 * @param analysisResult
	 *            Analysis structure
	 * @return Count of dependency
	 */
	private String formatDependency(DsmCell dep, AnalysisResult analysisResult) {
		if (!dep.isValid()) {
			return "x";
		}
		if (dep.getDependencyWeight() == 0) {
			return "";
		}
		String s = Integer.toString(dep.getDependencyWeight());
		if (!analysisResult.getViolations(dep.getDependency(), Severity.error)
				.isEmpty()) {
			s = s + "C";
		}
		return s;
	}

	/**
	 * Print Title of matrix
	 */
	private void printEmptyMatrixTitle() {
		htmlContent.append(templ.td_() + templ.td(""));
	}

	/**
	 * Print row headers of matrix (icon with package name or class name)
	 * 
	 * @param rowId
	 *            Row index
	 * @param aName
	 *            Package name or class name
	 * @param pkgCount
	 *            Count of classes in package
	 * @param aIsPackages
	 *            Matrix of packages or classes
	 */
	private void printRowHeader(int rowId, String aName, int pkgCount,
			boolean aIsPackages) {
		String linkTag;
		if (aIsPackages) {
			linkTag = templ.a("", aName + htmlFormat, aName, "")
					+ formatName(aName, 40) + templ.a_();
			linkTag = templ.img("", packageIconPath, "") + linkTag;
		} else {
			linkTag = templ.img("", classIconpath, "") + aName;
		}
		htmlContent.append(templ.tr("") + templ.td("packageName_rows")
				+ linkTag + " (" + pkgCount + ")" + templ.td_()
				+ templ.td("packageNumber_rows") + rowId + " ");
	}

	/**
	 * Truncate package or class name
	 * 
	 * @param name
	 *            Package or class name
	 * @param length
	 *            Maximum length of name
	 * @return Truncated name
	 */
	private String formatName(String name, int length) {
		if (name.length() - 2 <= length) {
			return name;
		}
		return ".." + name.substring(name.length() - length - 2);
	}

	/**
	 * Print cell content into the row
	 * 
	 * @param aCellContent
	 *            Cell content
	 */
	private void printCell(String aCellContent) {
		if (nextRow == 0) {
			htmlContent.append(templ.td_() + templ.td("packageName_cols"));
		} else {
			htmlContent.append(templ.td_() + templ.td(""));
		}
		htmlContent.append(String.format("%4s", aCellContent));
	}

	/**
	 * Print end row
	 */
	private void printEndRow() {
		htmlContent.append(templ.td_() + templ.tr_());
	}

	/**
	 * Convert StringBuildr to String and write to file
	 * 
	 * @param aPackageName
	 *            Package file name
	 */
	private void writeHtml(String aPackageName) {
		String sHtmlContent = htmlContent.toString();
		String filePath = System.getProperty("project.build.directory")
				+ "/site/DSM/" + aPackageName + htmlFormat;
		writeToFile(filePath, sHtmlContent);
	}

	public void writeToFile(String aFilePath, String aHtmlContent) {
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
}
