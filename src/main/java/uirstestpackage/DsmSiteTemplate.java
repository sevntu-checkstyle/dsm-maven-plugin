package uirstestpackage;

/**
 * @author
 * 
 */
public class DsmSiteTemplate {

	/**
	 * 
	 */
	public static String documentNavigationHeader = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
			+ "</head>";

	/**
	 * 
	 */
	public static String documentPackageNavigationBodyStart = "<body><b>Packages:</b><br/><ul><li><a target=\"summary\" href=\"./all_packages.html\">"
			+ "{icon_menu_all_pack} " + "All</a></li>";

	/**
	 * 
	 */
	public static String documentPackegeNavigationElement = "<li><a target=\"summary\" href=\"./{package_name}.html\">"
			+ "{icon_menu_pack} {package_name}</a></li>";

	/**
	 * 
	 */
	public static String documentPackageNavigationBodyEnd = "</ul></body>";

	/**
	 * 
	 */
	public static String documentPackagesHeader = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
			+ "</head>";

	/**
	 * 
	 */
	public static String documentPackagesBodyStart = "<body><h1><a target=\"summary\" href=\"./all_packages.html\">DSM Report</a> - "
			+ "{icon_all_pack} {aPackageName}</h1><table Cellpadding=\"0\" Cellspacing=\"0\" >\n<tr><td>";
	/**
	 * 
	 */
	public static String documentPackagesBodyEnd = "</table></body>";

	/**
	 * 
	 */
	public static String documentDsmHeader = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
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
			+ "</head>";

	/**
	 * 
	 */
	public static String documentDsmBodyStart = "<body><h1><a target=\"summary\" href=\"./all_packages.html\">DSM Report</a> - "
			+ "{icon_pack} {aPackageName}</h1><table Cellpadding=\"0\" Cellspacing=\"0\" >\n<tr><td>";

	/**
	 * 
	 */
	public static String documentDsmBodyEnd = "</table></body>";

	/**
	 * 
	 */
	public static String trTd = "</td> <td>";

	public static String DSMindexContent = "<frameset cols=\"25%,75%\">"
			+ "<frameset>"
			+ "<frame src=\"./packages.html\" name=\"packageList\" title=\"All Packages\">"
			+ "</frameset>"
			+ "<frame src=\"./all_packages.html\" name=\"summary\" title=\"Package, class and interface descriptions\" scrolling=\"yes\">"
			+ "</frameset>";
}
