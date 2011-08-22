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
 * @author yuriy
 * 
 */
public class DsmHtmlWriter {

	/**
	 * 
	 */
	private StringBuilder htmlContent = new StringBuilder();
	/**
	 * 
	 */
	private int nextRow = 0;

	/**
	 * 
	 */
	public DsmHtmlWriter() {
	}

	/**
	 * @param aPackageNames
	 */
	public void printNavigateDsmPackages(List<String> aPackageNames) {
		htmlContent = new StringBuilder();
		htmlContent.append(DsmSiteTemplate.documentNavigationHeader);
		htmlContent.append(DsmSiteTemplate.documentPackageNavigationBodyStart);
		for (int index = 0; index < aPackageNames.size(); index++) {
			String packageName = aPackageNames.get(index);
			packageName = DsmSiteTemplate.documentPackegeNavigationElement
					.replace("{package_name}", packageName);
			htmlContent.append(packageName);
		}
		htmlContent.append(DsmSiteTemplate.documentPackageNavigationBodyEnd);
		// String filePath = "/home/yuriy/workspace2/UIRS/site/packages.html";
		printToHTML("packages");
	}

	/**
	 * @param dsm
	 * @param analysisResult
	 * @param aPackageName
	 */
	public void printDsmPackages(Dsm dsm, AnalysisResult analysisResult,
			String aPackageName) {
		nextRow = 0;
		int i = 1;
		htmlContent = new StringBuilder();
		htmlContent.append(DsmSiteTemplate.documentPackagesHeader);
		htmlContent.append(DsmSiteTemplate.documentPackagesBodyStart.replace(
				"{aPackageName}", aPackageName));

		printColumnHeaders(dsm.getRows().size());
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printPackage(i++, row, analysisResult);
		}

		htmlContent.append(DsmSiteTemplate.documentPackagesBodyEnd);
		printToHTML(aPackageName);
	}

	/**
	 * @param dsm
	 * @param analysisResult
	 * @param aPackageName
	 */
	public void printDsm(Dsm dsm, AnalysisResult analysisResult,
			String aPackageName) {
		nextRow = 0;

		htmlContent = new StringBuilder();
		htmlContent.append(DsmSiteTemplate.documentDsmHeader);
		htmlContent.append(DsmSiteTemplate.documentDsmBodyStart.replace(
				"{aPackageName}", aPackageName));

		printColumnHeaders(dsm.getRows().size());
		int i = 1;
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printClasses(i++, row, analysisResult);
		}

		htmlContent.append(DsmSiteTemplate.documentDsmBodyEnd);
		printToHTML(aPackageName);
	}

	private void printColumnHeaders(int size) {
		printEmptyRowHeader();
		for (int i = 1; i <= size; i++)
			printCell(Integer.toString(i));
		nextRow();
	}

	private void printClasses(int index, DsmRow row,
			AnalysisResult analysisResult) {
		printRowHeader(index, row.getDependee().getDisplayName(), row
				.getDependee().getContentCount(), false);

		for (DsmCell dep : row.getCells())
			printCell(formatDependency(dep, analysisResult));
		nextRow();
	}

	private void printPackage(int index, DsmRow row,
			AnalysisResult analysisResult) {
		String packageName = row.getDependee().getDisplayName();

		printRowHeader(index, packageName, row.getDependee().getContentCount(),
				true);

		for (DsmCell dep : row.getCells())
			printCell(formatDependency(dep, analysisResult));
		nextRow();
	}

	private String formatDependency(DsmCell dep, AnalysisResult analysisResult) {
		if (!dep.isValid())
			return "x";
		if (dep.getDependencyWeight() == 0)
			return "";
		String s = Integer.toString(dep.getDependencyWeight());
		// FIXME: Severity.error might mean something else than a cycle, we
		// should change the UI and show an 'E' instead
		if (!analysisResult.getViolations(dep.getDependency(), Severity.error)
				.isEmpty())
			s = s + "C";
		return s;
	}

	private void printEmptyRowHeader() {
		htmlContent.append(String.format(
				"%3s" + DsmSiteTemplate.trTd + "%46s ", "", ""));
	}

	private void printRowHeader(int rowId, String aName, int pkgCount,
			boolean aIsPackage) {
		String name = aName;
		if (aIsPackage) {
			name = "<a class=\"\" href=\"./" + name + ".html\" title=\"" + name
					+ "\">" + "{icon_pack} " + formatName(name, 40) + "</a>";
		} else {
			name = "{icon_class} " + aName;
		}
		htmlContent
				.append(String
						.format("<tr class=\"element\"> <td class=\"packageName_rows\">%40s (%3s)</td><td class=\"packageNumber_rows\">%3s ",
								new Object[] { name, pkgCount, rowId }));
	}

	private String formatName(String name, int length) {
		if (name.length() <= length)
			return name;
		return ".." + name.substring(name.length() - 38);
	}

	private void printCell(String content) {
		if (nextRow == 0) {
			htmlContent.append("</td><td class=\"packageName_cols\">"
					+ String.format("%4s", content));
		} else {

			htmlContent.append("</td><td class=\"element_" + 1 + "\">"
					+ String.format("%4s", content));
		}
	}

	private void nextRow() {
		htmlContent.append("</td></tr>\n");
	}

	private void printToHTML(String aPackageName) {
		String sHtmlContent = htmlContent.toString();
		sHtmlContent = sHtmlContent.replace("{icon_pack}",
				"<img src=\"./images/package.png\" alt=\"\" />");
		sHtmlContent = sHtmlContent.replace("{icon_class}",
				"<img src=\"./images/class.png\" alt=\"\" />");
		sHtmlContent = sHtmlContent.replace("{icon_menu_all_pack}",
				"<img src=\"./images/packages.png\" alt=\"\" />");
		sHtmlContent = sHtmlContent.replace("{icon_menu_pack}",
				"<img src=\"./images/package.png\" alt=\"\" />");
		sHtmlContent = sHtmlContent.replace("{icon_all_pack}",
				"<img src=\"./images/packages.png\" alt=\"\" />");

		// String filePath = "/home/yuriy/workspace2/UIRS/site/" + aPackageName
		// + ".html";
		String filePath = System.getProperty("user.dir") + "/target/site/DSM/"
				+ aPackageName + ".html";

		System.out.println("File paths:" + filePath);
		
		System.out.println();
		//System.out.println(sHtmlContent);
		writeToFile(filePath, sHtmlContent);
	}

	public void writeToFile(String filename, String aHtmlContent) {

		BufferedWriter bufferedWriter = null;

		try {

			// Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(filename));

			// Start writing to the output stream
			bufferedWriter.write(aHtmlContent);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			// Close the BufferedWriter
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
