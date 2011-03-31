package uirstestpackage;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation.Severity;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.textui.Writer;

public class DsmHtmlWriter {

	private StringBuilder htmlContent = new StringBuilder();
	private int nextRow = 0;

	public DsmHtmlWriter() {
	}

	public void printNavigateDsmPackages(List<String> aPackageNames) {
		htmlContent = new StringBuilder();
		htmlContent
				.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
						+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"
						+ "<head>"
						+ "<title></title>"
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />"
						+ "<meta name=\"ROBOTS\" content=\"index,follow\" />"
						+ "<meta name=\"TITLE\" content=\"\" />"
						+ "<meta name=\"DESCRIPTION\" content=\"\" />"
						+ "<meta name=\"KEYWORDS\" content=\"\" />"
						+ "<meta name=\"AUTHOR\" content=\"\" />"
						+ "<meta name=\"Copyright\" content=\"\" />"
						+ "<meta http-equiv=\"Content-Language\" content=\"en\" />"
						+ "<style type=\"text/css\"> @import url(css/style.css);</style>"
						+ "</head>" + "<body>");
		htmlContent.append("<b>Packages:</b><br/><ul>");
		htmlContent
				.append("<li><a target=\"summary\" href=\"./all_packages.html\">"
						+ "{icon_menu_all_pack} " + "All</a></li>");
		for (int index = 0; index < aPackageNames.size(); index++) {
			String packageName = aPackageNames.get(index);
			packageName = "<li>" 
					+ "<a target=\"summary\" href=\"./" + packageName
					+ ".html\">" + "{icon_menu_pack} " + packageName + "</a></li>";
			htmlContent.append(packageName);
		}
		htmlContent.append("</ul>");
		htmlContent.append("</body>");
		//String filePath = "/home/yuriy/workspace2/UIRS/site/packages.html";
		printToHTML("packages");
		//writeToFile(filePath, stringBuilder.toString());
	}

	public void printDsmPackages(Dsm dsm, AnalysisResult analysisResult,
			String aPackageName) {
		htmlContent = new StringBuilder();
		htmlContent
				.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
						+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"
						+ "<head>"
						+ "<title></title>"
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />"
						+ "<meta name=\"ROBOTS\" content=\"index,follow\" />"
						+ "<meta name=\"TITLE\" content=\"\" />"
						+ "<meta name=\"DESCRIPTION\" content=\"\" />"
						+ "<meta name=\"KEYWORDS\" content=\"\" />"
						+ "<meta name=\"AUTHOR\" content=\"\" />"
						+ "<meta name=\"Copyright\" content=\"\" />"
						+ "<meta http-equiv=\"Content-Language\" content=\"en\" />"
						+ "<style type=\"text/css\"> @import url(css/style.css);</style>"
						+ "</head>" + "<body>");

		htmlContent
				.append("<h1><a target=\"summary\" href=\"./all_packages.html\">DSM Report</a> - "
						+ "{icon_all_pack} " + aPackageName + "</h1>");
		htmlContent
				.append("<table Cellpadding=\"0\" Cellspacing=\"0\" >\n<tr><td>");

		nextRow = 0;
		printColumnHeaders(dsm.getRows().size());
		int i = 1;
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printPackage(i++, row, analysisResult);
		}

		htmlContent.append("</table>");
		htmlContent.append("</body>");

		printToHTML(aPackageName);
	}

	public void printDsm(Dsm dsm, AnalysisResult analysisResult,
			String aPackageName) {
		htmlContent = new StringBuilder();
		htmlContent
				.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
						+ "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">"
						+ "<head>"
						+ "<title></title>"
						+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />"
						+ "<meta name=\"ROBOTS\" content=\"index,follow\" />"
						+ "<meta name=\"TITLE\" content=\"\" />"
						+ "<meta name=\"DESCRIPTION\" content=\"\" />"
						+ "<meta name=\"KEYWORDS\" content=\"\" />"
						+ "<meta name=\"AUTHOR\" content=\"\" />"
						+ "<meta name=\"Copyright\" content=\"\" />"
						+ "<meta http-equiv=\"Content-Language\" content=\"en\" />"
						+ "<style type=\"text/css\"> @import url(css/style.css);</style>"
						+ "</head>" + "<body>");

		htmlContent
				.append("<h1><a target=\"summary\" href=\"./all_packages.html\">DSM Report</a> - "
						+ "{icon_pack} " + aPackageName + "</h1>");
		htmlContent
				.append("<table Cellpadding=\"0\" Cellspacing=\"0\" >\n<tr><td>");

		nextRow = 0;
		printColumnHeaders(dsm.getRows().size());
		int i = 1;
		for (DsmRow row : dsm.getRows()) {
			nextRow++;
			printClasses(i++, row, analysisResult);
		}

		htmlContent.append("</table>");
		htmlContent.append("</body>");

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
		// print(String.format("%51s", ""));
		htmlContent.append(String.format("%3s</td> <td>%46s ", "", ""));
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
		// println("|");
		htmlContent.append("</td></tr>\n");
	}

	private void print(String s) {
		System.out.print(s);
	}

	private void println(String s) {
		System.out.println(s);
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
		String filePath = "/home/yuriy/workspace2/UIRS/site/" + aPackageName
				+ ".html";
		System.out.println(sHtmlContent);
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
