package uirstestpackage;

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
public class DsmHtmlWriter implements DsmSiteTemplate {
	private String siteHeaders = "SiteHeaders.html";
	private String documentPackagesHeader = "documentPackagesHeader.html";
	private String documentDSMHeader = "documentDSMHeader.html";
	private String htmlFormat = ".html";
	private String allPackagesFilePath = "./all_packages.html";
	private String packageIconPath = "./images/package.png";
	private String menuPackageIconPath = "./images/package.png";
	private String packagesIconPath = "./images/packages.png";
	private String allPackageIconPath = "./images/packages.png";
	private String classIconpath = "./images/class.png";
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
		htmlContent.append(String.format(
				DOCUMENT_PACKAGE_NAVIGATION_BODY_START, allPackagesFilePath,
				String.format(IMG_TAG, packagesIconPath, "")));
		for (int index = 0; index < aPackageNames.size(); index++) {
			String packageName = aPackageNames.get(index);
			packageName = String.format(DOCUMENT_PACKEGE_NAVIGATION_ELEMENT,
					packageName,
					String.format(IMG_TAG, menuPackageIconPath, ""),
					packageName);
			htmlContent.append(packageName);
		}
		htmlContent.append(DOCUMENT_PACKAGE_NAVIGATION_BODY_END);
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
		htmlContent.append(String.format(DOCUMENT_BODY_START,
				allPackagesFilePath,
				String.format(IMG_TAG, allPackageIconPath, ""), aPackageName));

		printColumnHeaders(dsm.getRows().size());

		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printPackage(i++, row, analysisResult);
		}

		htmlContent.append(DOCUMENT_PACKAGE_NAVIGATION_BODY_END);
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
		htmlContent.append(String.format(DOCUMENT_BODY_START,
				allPackagesFilePath,
				String.format(IMG_TAG, packageIconPath, ""), aPackageName));
		printColumnHeaders(dsm.getRows().size());
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printClasses(i++, row, analysisResult);
		}
		htmlContent.append(END_TABLE_END_BODY);
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
		htmlContent.append(String.format("%3s" + TR_TD + "%46s ", "", ""));
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
			linkTag = String.format(LINK_TAG_A, "", aName + htmlFormat, aName,
					formatName(aName, 40));
			linkTag = String.format(IMG_TAG, packageIconPath, "") + linkTag;
		} else {
			linkTag = String.format(IMG_TAG, classIconpath, "") + aName;
		}
		htmlContent.append(String.format(PACKAGE_ROW, new Object[] { linkTag,
				pkgCount, rowId }));
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
		if (name.length() <= length) {
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
			htmlContent.append(END_TD_TD_PACKAGENAMECOLS);
		} else {
			htmlContent.append(TR_TD);
		}
		htmlContent.append(String.format("%4s", aCellContent));
	}

	/**
	 * Print end row
	 */
	private void printEndRow() {
		htmlContent.append(END_TD_END_TR);
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

	@Override
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
