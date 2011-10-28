package uirstestpackage;

/**
 * Site constants.
 * 
 * @author
 * 
 */
public interface DsmSiteTemplate {
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
