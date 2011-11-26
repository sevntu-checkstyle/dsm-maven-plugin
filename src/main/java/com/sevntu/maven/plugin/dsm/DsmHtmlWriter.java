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
	private final String siteHeaders = "SiteHeaders.html";
	private final String documentPackagesHeader = "documentPackagesHeader.html";
	private final String documentDSMHeader = "documentDSMHeader.html";
	private final String htmlFormat = ".html";
	private final String linkTarget = "summary";
	private final String allPackagesFilePath = "./all_packages.html";
	private final String packageIconPath = "./images/package.png";
	private final String menuPackageIconPath = packageIconPath;
	private final String packagesIconPath = packageIconPath;
	private final String allPackageIconPath = packageIconPath;
	private final String classIconpath = "./images/class.png";

	private TemplateEngine templ = new TemplateEngine();

	private int nextRow = 0;

	/**
	 * Print navigation on site by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	public void printNavigateDsmPackages(List<String> aPackageNames) {
		StringBuilder htmlContent = new StringBuilder();

		/* Add headers */
		htmlContent.append(addHeaderContent(siteHeaders));

		/* Body start */
		htmlContent.append(templ.body(""));

		/* Title of menu */
		htmlContent.append(templ.b("") + "Packages:" + templ.b_());

		/* Packages menu start */
		htmlContent.append(templ.ul(""));

		/* First link in packages menu. Link to DSM of all packages. */
		htmlContent.append(templ.li("")
				+ templ.a("", allPackagesFilePath, "", linkTarget)
				+ templ.img("", packagesIconPath, "") + " All" + templ.a_()
				+ templ.li_());

		for (int index = 0; index < aPackageNames.size(); index++) {
			String packageName = aPackageNames.get(index);

			/* Next link to DSM of some package */
			htmlContent.append(templ.li("")
					+ templ.a("", "./" + packageName + htmlFormat, "",
							linkTarget)
					+ templ.img("", menuPackageIconPath, "") + " "
					+ packageName + templ.a_() + templ.li_());
		}

		/* Packages menu end */
		htmlContent.append(templ.ul_());

		/* Body end */
		htmlContent.append(templ.body_());

		/* Write content to file and save it */
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

		/* Add headers */
		htmlContent.append(addHeaderContent(documentPackagesHeader));

		/* Body start */
		htmlContent.append(templ.body(""));

		/* Add DSM Title */
		htmlContent.append(templ.h1("")
				+ templ.a("", allPackagesFilePath, "", linkTarget)
				+ "DSM Report" + templ.a_() + " - "
				+ templ.img("", allPackageIconPath, "") + " " + aPackageName
				+ templ.h1_());

		/* Start table of DSM */
		htmlContent.append(templ.table(""));

		printColumnHeaders(dsm.getRows().size(), htmlContent);

		int packageIndex = 1;
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printPackage(packageIndex++, row, analysisResult, htmlContent);
		}

		/* End table of DSM */
		htmlContent.append(templ.table_());

		/* Body end */
		htmlContent.append(templ.body_());

		/* Write content to file and save it */
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
		int i = 1;
		nextRow = 0;
		StringBuilder htmlContent = new StringBuilder();

		htmlContent.append(addHeaderContent(documentDSMHeader));
		/* Body start */
		htmlContent.append(templ.body(""));

		/* Add DSM Title */
		htmlContent.append(templ.h1("")
				+ templ.a("", allPackagesFilePath, "", linkTarget)
				+ "DSM Report" + templ.a_() + " - "
				+ templ.img("", packageIconPath, "") + " " + aPackageName
				+ templ.h1_());

		/* Start table of DSM */
		htmlContent.append(templ.table(""));

		printColumnHeaders(dsm.getRows().size(), htmlContent);

		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printClasses(i++, row, analysisResult, htmlContent);
		}

		/* End table of DSM */
		htmlContent.append(templ.table_());

		/* Body end */
		htmlContent.append(templ.body_());

		/* Write content to file and save it */
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
		/* start row */
		htmlContent.append(templ.tr(""));

		htmlContent.append(templ.td("") + templ.td_());
		htmlContent.append(templ.td("") + templ.td_());

		for (int i = 1; i <= size; i++) {
			printCell(Integer.toString(i), htmlContent);
		}

		htmlContent.append(templ.td_() + templ.tr_());
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

		htmlContent.append(templ.tr(""));

		printRowHeader(index, row.getDependee().getDisplayName(), row
				.getDependee().getContentCount(), false, htmlContent);

		for (DsmCell dep : row.getCells()) {
			printCell(formatDependency(dep, analysisResult), htmlContent);
		}

		htmlContent.append(templ.tr_());
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

		/* start row */
		htmlContent.append(templ.tr(""));

		/* print name of package */
		printRowHeader(index, packageName, row.getDependee().getContentCount(),
				true, htmlContent);

		/* print count of dependency */
		for (DsmCell dep : row.getCells()) {
			printCell(formatDependency(dep, analysisResult), htmlContent);
		}

		/* end row */
		htmlContent.append(templ.tr_());
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

		htmlContent.append(templ.td("packageName_rows"));

		if (aIsPackages) {

			htmlContent.append(templ.img("", packageIconPath, ""));

			htmlContent.append(templ.a("", aName + htmlFormat, aName, "")
					+ formatName(aName, 40) + templ.a_());
		} else {

			htmlContent.append(templ.img("", classIconpath, ""));

			htmlContent.append(aName);
		}

		htmlContent.append(" (" + pkgCount + ")");

		htmlContent.append(templ.td_());

		htmlContent
				.append(templ.td("packageNumber_rows") + rowId + templ.td_());
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
			htmlContent.append(templ.td("packageName_cols") + aCellContent
					+ templ.td_());
		} else {
			htmlContent.append(templ.td("") + aCellContent + templ.td_());
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
