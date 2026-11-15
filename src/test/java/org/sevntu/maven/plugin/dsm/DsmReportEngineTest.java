package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.hjug.dtangler.core.dsmengine.DsmEngine;
import org.junit.Test;


public class DsmReportEngineTest {

	/**
	 * Link to the page of package in packages_menu.ftl
	 */
	private static final String PACKAGE_IN_NAVIGATION = "href=\"\\./([^\"]+)\\.html\"";

	/**
	 * Name of package in the row of packages_page.ftl
	 */
	private static final String PACKAGE_IN_ROW = "<a href=\"[^\"]+\" title=\"([^\"]+)\" target=\"\"";

	/**
	 * Name of package in the column of packages_page.ftl
	 */
	private static final String PACKAGE_IN_COLUMN = "packageName_cols\" title=\"([^\"]+)\"";

	/**
	 * Names of column and row together with the content of cell in packages_page.ftl
	 */
	private static final String CELL = "title=\"([^\"]+ (?:uses|has cycle dependency with) [^\"]+\">\\s*\\w+)";

	/**
	 * Name of class in the row of classes_page.ftl
	 */
	private static final String CLASS_IN_ROW = "class\\.png\" alt=\"([^\"]+)\"";

	@Test
	public void setDsmReportSiteDirectoryTest() {
		Exception ex = null;
		DsmReportEngine dsmReport = new DsmReportEngine();
		try {
			dsmReport.setOutputDirectory(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Dsm directory is empty.", e.getMessage());
		}
		assertNotNull(ex);
	}


	@Test
	public void setSourceDirectoryTest() {
		Exception ex = null;
		DsmReportEngine dsmReport = new DsmReportEngine();
		try {
			dsmReport.setSourceDirectory(null);
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Source directory path can't be empty.", e.getMessage());
		}

		try {
			dsmReport.setSourceDirectory("");
			Assert.fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Source directory path can't be empty.", e.getMessage());
		}

		try {
			dsmReport.setSourceDirectory("/not/exists/directory");
			Assert.fail();
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Source directory '/not/exists/directory' not exists", e.getMessage());
		}

		assertNotNull(ex);
	}


	@Test
	public void reportSortByNameTest() throws Exception {
		// jar of Dtangler is a handy sample with a lot of packages that have the same prefix
		File input = new File(DsmEngine.class.getProtectionDomain().getCodeSource().getLocation().toURI());

		File defaultReport = createReport(input, "target/testDir/defaultOrder", false);
		File sortedReport = createReport(input, "target/testDir/sortByName", true);

		List<String> defaultPackages = getPackagesFromNavigation(defaultReport);
		List<String> sortedPackages = getPackagesFromNavigation(sortedReport);
		List<String> expectedPackages = new ArrayList<String>(defaultPackages);
		Collections.sort(expectedPackages, String.CASE_INSENSITIVE_ORDER);

		assertTrue("Sample should have several packages", defaultPackages.size() > 1);
		assertFalse("Order of Dtangler should differ from the order by name",
				expectedPackages.equals(defaultPackages));
		assertEquals(expectedPackages, sortedPackages);

		// rows and columns of the matrix are in the same order as the navigation
		File defaultMatrix = new File(defaultReport, "all_packages.html");
		File sortedMatrix = new File(sortedReport, "all_packages.html");
		assertEquals(sortedPackages, findAll(sortedMatrix, PACKAGE_IN_ROW));
		assertEquals(sortedPackages, findAll(sortedMatrix, PACKAGE_IN_COLUMN));

		// cells are only moved, every number still belongs to the same pair of packages
		List<String> defaultCells = findAll(defaultMatrix, CELL);
		List<String> sortedCells = findAll(sortedMatrix, CELL);
		Collections.sort(defaultCells);
		Collections.sort(sortedCells);
		assertFalse(sortedCells.isEmpty());
		assertEquals(defaultCells, sortedCells);

		for (String packageName : sortedPackages) {
			List<String> classes = findAll(new File(sortedReport, packageName + ".html"), CLASS_IN_ROW);
			List<String> expectedClasses = new ArrayList<String>(classes);
			Collections.sort(expectedClasses, String.CASE_INSENSITIVE_ORDER);

			assertFalse(classes.isEmpty());
			assertEquals(expectedClasses, classes);
		}
	}


	private static File createReport(File aInput, String aOutputDirectory, boolean aSortByName)
			throws Exception {
		DsmReportEngine dsmReport = new DsmReportEngine();
		dsmReport.setSourceDirectory(aInput.getAbsolutePath());
		dsmReport.setOutputDirectory(aOutputDirectory);
		dsmReport.setSortByName(aSortByName);
		dsmReport.report();
		return new File(aOutputDirectory);
	}


	private static List<String> getPackagesFromNavigation(File aReport) throws Exception {
		List<String> packages = findAll(new File(aReport, "packages.html"), PACKAGE_IN_NAVIGATION);
		// the first link is not a package
		assertEquals("all_packages", packages.remove(0));
		return packages;
	}


	private static List<String> findAll(File aFile, String aRegex) throws Exception {
		String content = new String(Files.readAllBytes(aFile.toPath()), StandardCharsets.UTF_8);
		Matcher matcher = Pattern.compile(aRegex).matcher(content);
		List<String> result = new ArrayList<String>();
		while (matcher.find()) {
			result.add(matcher.group(1));
		}
		return result;
	}
}
