package com.sevntu.maven.plugin.dsm;

import java.io.BufferedWriter;
import java.io.File;
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
	private final String siteHeaders = "SiteHeaders.html";
	private final String documentPackagesHeader = siteHeaders;
	private final String documentDSMHeader = siteHeaders;
	private final String htmlFormat = ".html";
	private final String linkTarget = "summary";
	private final String allPackagesFilePath = "." + File.separator
			+ "all_packages.html";
	private final String packageIconPath = "." + File.separator
			+ "images/package.png";
	private final String menuPackageIconPath = packageIconPath;
	private final String packagesIconPath = packageIconPath;
	private final String allPackageIconPath = packageIconPath;
	private final String classIconpath = "." + File.separator + "images"
			+ File.separator + "class.png";


	private int nextRow = 0;

	/**
	 * Print navigation on site by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	public void printNavigateDsmPackages(List<String> aPackageNames) {
		StringBuilder htmlContent = new StringBuilder();

		// Add headers 
		htmlContent.append(addHeaderContent(siteHeaders));

		// Body start 
		htmlContent.append(TagFactory.body(""));

		// Title of menu 
		htmlContent.append(TagFactory.b("") + "Packages:" + TagFactory.bEnd());

		// Packages menu start
		htmlContent.append(TagFactory.ul(""));

		// First link in packages menu. Link to DSM of all packages.
		htmlContent.append(TagFactory.li("")
				+ TagFactory.a("", allPackagesFilePath, "", linkTarget)
				+ TagFactory.img("", packagesIconPath, "") + " All" + TagFactory.aEnd()
				+ TagFactory.liEnd());

		for (int index = 0; index < aPackageNames.size(); index++) {
			String packageName = aPackageNames.get(index);

			// Next link to DSM of some package
			htmlContent.append(TagFactory.li("")
					+ TagFactory.a("", "." + File.separator + packageName
							+ htmlFormat, "", linkTarget)
					+ TagFactory.img("", menuPackageIconPath, "") + " "
					+ packageName + TagFactory.aEnd() + TagFactory.liEnd());
		}

		// Packages menu end
		htmlContent.append(TagFactory.ulEnd());

		// Body end
		htmlContent.append(TagFactory.bodyEnd());

		// Write content to file and save it
		writeHtml("packages", htmlContent);
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
		StringBuilder htmlContent = new StringBuilder();

		// Add headers
		htmlContent.append(addHeaderContent(documentPackagesHeader));

		// Body start
		htmlContent.append(TagFactory.body(""));

		// Add DSM Title
		htmlContent.append(TagFactory.h1("")
				+ TagFactory.a("", allPackagesFilePath, "", linkTarget)
				+ "DSM Report" + TagFactory.aEnd() + " - "
				+ TagFactory.img("", allPackageIconPath, "") + " " + aPackageName
				+ TagFactory.h1End());

		// Start table of DSM
		htmlContent.append(TagFactory.table(""));

		printColumnHeaders(dsm.getRows().size(), htmlContent);

		int packageIndex = 1;
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printPackage(packageIndex++, row, analysisResult, htmlContent);
		}

		// End table of DSM
		htmlContent.append(TagFactory.tableEnd());

		// Body end
		htmlContent.append(TagFactory.bodyEnd());

		// Write content to file and save it
		writeHtml(aPackageName, htmlContent);
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
		nextRow = 0;
		StringBuilder htmlContent = new StringBuilder();

		htmlContent.append(addHeaderContent(documentDSMHeader));
		// Body start
		htmlContent.append(TagFactory.body(""));

		// Add DSM Title
		htmlContent.append(TagFactory.h1("")
				+ TagFactory.a("", allPackagesFilePath, "", linkTarget)
				+ "DSM Report" + TagFactory.aEnd() + " - "
				+ TagFactory.img("", packageIconPath, "") + " " + aPackageName
				+ TagFactory.h1End());

		// Start table of DSM
		htmlContent.append(TagFactory.table(""));

		printColumnHeaders(dsm.getRows().size(), htmlContent);

		int rowIndex = 1;
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printClasses(rowIndex++, row, analysisResult, htmlContent);
		}

		// End table of DSM
		htmlContent.append(TagFactory.tableEnd());

		// Body end
		htmlContent.append(TagFactory.bodyEnd());

		// Write content to file and save it
		writeHtml(aPackageName, htmlContent);
	}

	/**
	 * Print columns index
	 * 
	 * @param size
	 *            Count of columns
	 */
	private void printColumnHeaders(final int size,
			final StringBuilder htmlContent) {
		// start row
		htmlContent.append(TagFactory.tr(""));

		htmlContent.append(TagFactory.td("") + TagFactory.tdEnd());
		htmlContent.append(TagFactory.td("") + TagFactory.tdEnd());

		for (int i = 1; i <= size; i++) {
			printCell(Integer.toString(i), htmlContent);
		}

		htmlContent.append(TagFactory.tdEnd() + TagFactory.trEnd());
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
	private void printClasses(final int index, final DsmRow row,
			final AnalysisResult analysisResult, final StringBuilder htmlContent) {

		htmlContent.append(TagFactory.tr(""));

		printRowHeader(index, row.getDependee().getDisplayName(), row
				.getDependee().getContentCount(), false, htmlContent);

		for (DsmCell dep : row.getCells()) {
			printCell(formatDependency(dep, analysisResult), htmlContent);
		}

		htmlContent.append(TagFactory.trEnd());
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
	private void printPackage(final int index, final DsmRow row,
			final AnalysisResult analysisResult, final StringBuilder htmlContent) {
		String packageName = row.getDependee().getDisplayName();

		// start row
		htmlContent.append(TagFactory.tr(""));

		// print name of package
		printRowHeader(index, packageName, row.getDependee().getContentCount(),
				true, htmlContent);

		// print count of dependency
		for (DsmCell dep : row.getCells()) {
			printCell(formatDependency(dep, analysisResult), htmlContent);
		}

		// end row
		htmlContent.append(TagFactory.trEnd());
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
			boolean aIsPackages, final StringBuilder htmlContent) {

		htmlContent.append(TagFactory.td("packageName_rows"));

		if (aIsPackages) {

			htmlContent.append(TagFactory.img("", packageIconPath, ""));

			htmlContent.append(TagFactory.a("", aName + htmlFormat, aName, "")
					+ formatName(aName, 40) + TagFactory.aEnd());
		} else {

			htmlContent.append(TagFactory.img("", classIconpath, ""));

			htmlContent.append(aName);
		}

		htmlContent.append(" (" + pkgCount + ")");

		htmlContent.append(TagFactory.tdEnd());

		htmlContent
				.append(TagFactory.td("packageNumber_rows") + rowId + TagFactory.tdEnd());
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
	private void printCell(String aCellContent, final StringBuilder htmlContent) {
		if (nextRow == 0) {
			htmlContent.append(TagFactory.td("packageName_cols") + aCellContent
					+ TagFactory.tdEnd());
		} else {
			htmlContent.append(TagFactory.td("") + aCellContent + TagFactory.tdEnd());
		}
	}

	/**
	 * Convert StringBuildr to String and write to file
	 * 
	 * @param aPackageName
	 *            Package file name
	 */
	private void writeHtml(String aPackageName, StringBuilder htmlContent) {
		String sHtmlContent = htmlContent.toString();
		String filePath = System.getProperty("project.build.directory")
				+ File.separator + "site" + File.separator + "DSM"
				+ File.separator + aPackageName + htmlFormat;
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
