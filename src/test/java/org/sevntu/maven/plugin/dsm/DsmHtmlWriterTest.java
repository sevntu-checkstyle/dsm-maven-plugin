package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;
import org.junit.Test;


public class DsmHtmlWriterTest {

	@Test
	public void dsmHtmlWriterTest() {
		Exception ex = null;
		try {
			new DsmHtmlWriter(null);
		} catch (Exception e) {
			ex = e;
			assertEquals("Path to the report directory should not be null or empty", e.getMessage());
		}
		assertNotNull(ex);

		ex = null;
		try {
			new DsmHtmlWriter(" ");
		} catch (Exception e) {
			ex = e;
			assertEquals("Path to the report directory should not be null or empty", e.getMessage());
		}
		assertNotNull(ex);
	}


	@Test
	public void isEmptyStringTest() {
		assertEquals(true, DsmHtmlWriter.isNullOrEmpty(null));
		assertEquals(true, DsmHtmlWriter.isNullOrEmpty(""));
		assertEquals(true, DsmHtmlWriter.isNullOrEmpty("  "));
		assertEquals(false, DsmHtmlWriter.isNullOrEmpty("asd "));
	}


	@Test
	public void printDsmTest() {
		DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter("target/testDir");
		Exception ex = null;
		try {
			dsmHtmlWriter.printDsm(null, null, null, null);
		} catch (Exception e) {
			ex = e;
			assertEquals("DSM structure should not be null", e.getMessage());
		}
		assertNotNull(ex);

		ex = null;
		try {
			dsmHtmlWriter.printDsm(new Dsm(new ArrayList<DsmRow>()), null, null, null);
		} catch (Exception e) {
			ex = e;
			assertEquals("Analysis structure should not be null", e.getMessage());
		}
		assertNotNull(ex);

		ex = null;
		try {
			dsmHtmlWriter.printDsm(new Dsm(new ArrayList<DsmRow>()), new AnalysisResult(
					new HashMap<Dependency, Set<Violation>>(), new HashSet<Violation>(), true),
					null, null);
		} catch (Exception e) {
			ex = e;
			assertEquals("Title of DSM should not be empty", e.getMessage());
		}
		assertNotNull(ex);
	}


	@Test
	public void printNavigateDsmPackagesTest() {
		DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter("target/testDir");
		Exception ex = null;
		try {
			dsmHtmlWriter.printNavigateDsmPackages(null);
		} catch (Exception e) {
			ex = e;
			assertEquals("List of package names should not be null", e.getMessage());
		}
		assertNotNull(ex);
	}


	@Test
	public void processTemplate() {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		String templateName = DsmHtmlWriter.FTL_CLASSES_PAGE;
		DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter("/");
		String result = "";

		List<Integer> headerIndexes = new ArrayList<Integer>();
		List<DsmRowModel> dsmRowDatas = new ArrayList<DsmRowModel>();
		List<String> numberOfDependencies = new ArrayList<String>();

		headerIndexes.add(1);
		headerIndexes.add(2);

		numberOfDependencies.add("1");
		numberOfDependencies.add("2");

		dsmRowDatas.add(new DsmRowModel(1, "first name", 2, numberOfDependencies));
		dsmRowDatas.add(new DsmRowModel(2, "second name", 3, numberOfDependencies));

		dataModel.put("title", "My title.");
		dataModel.put("headerIndexes", headerIndexes);
		dataModel.put("rows", dsmRowDatas);
		try {
			result = dsmHtmlWriter.processTemplate(dataModel, templateName).toString();
		} catch (Exception e) {
			assertNotNull(e);
		}

		assertTrue(result.indexOf("<td class=\"packageName_cols\">1</td>") > -1);
		assertTrue(result.indexOf("<td class=\"packageName_cols\">2</td>") > -1);
		assertTrue(result.indexOf("first name") > -1);
		assertTrue(result.indexOf("second name") > -1);
		assertTrue(result.indexOf("<td class=\"packageNumber_rows\">1</td>") > -1);
		assertTrue(result.indexOf("<td class=\"packageNumber_rows\">2</td>") > -1);
		assertTrue(result.indexOf("<td>1</td>") > -1);
		assertTrue(result.indexOf("<td>2</td>") > -1);
	}
}
