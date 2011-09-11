package uirstestpackage;

/**
 * Site constants.
 * 
 * @author
 * 
 */
public interface DsmSiteTemplate {
	String DOCUMENT_PACKAGE_NAVIGATION_BODY_START = "<body><b>Packages:</b><br/>"
			+ "<ul><li><a target=\"summary\" href=\"%s\">" + "%s All</a></li>";

	String DOCUMENT_PACKEGE_NAVIGATION_ELEMENT = "<li><a target=\"summary\" href=\"./%s.html\">"
			+ "%s %s</a></li>";

	String DOCUMENT_PACKAGE_NAVIGATION_BODY_END = "</ul></body>";

	String DOCUMENT_BODY_START = "<body>"
			+ "<h1><a target=\"summary\" href=\"%s\">DSM Report</a> - "
			+ "%s %s</h1><table Cellpadding=\"0\" Cellspacing=\"0\" ><tr><td>";

	String LINK_TAG_A = "<a class=\"%s\" href=\"%s\" title=\"%s\">%s</a>";

	String PACKAGE_ROW = "<tr><td class=\"packageName_rows\">%40s (%3s)</td><td class=\"packageNumber_rows\">%3s ";

	String END_TABLE_END_BODY = "</table></body>";

	String TR_TD = "</td><td>";

	String END_TD_END_TR = "</td></tr>";

	String END_TD_TD_PACKAGENAMECOLS = "</td><td class=\"packageName_cols\">";

	String IMG_TAG = "<img src=\"%s\" alt=\"%s\" />";

	/**
	 * Write site content to file.
	 * 
	 * @param aFilePath
	 *            File path
	 * @param aHtmlContent
	 *            File content
	 */
	void writeToFile(String aFilePath, String aHtmlContent);
}
