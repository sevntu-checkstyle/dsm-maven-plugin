package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.maven.plugin.MojoExecutionException;
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
		assertEquals(true, DsmHtmlWriter.isEmptyString(null));
		assertEquals(true, DsmHtmlWriter.isEmptyString(""));
		assertEquals(true, DsmHtmlWriter.isEmptyString("  "));
		assertEquals(false, DsmHtmlWriter.isEmptyString("asd "));
	}


	@Test
	public void printDsmTest() throws MojoExecutionException {
		DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter("testDir");
		Exception ex = null;
		try {
			dsmHtmlWriter.printDsm(null, null, null, null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("DSM structure should not be null", e.getMessage());
		}
		assertNotNull(ex);

		ex = null;
		try {
			dsmHtmlWriter.printDsm(new Dsm(new ArrayList<DsmRow>()), null, null, null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Analysis structure should not be null", e.getMessage());
		}
		assertNotNull(ex);

		ex = null;
		try {
			dsmHtmlWriter.printDsm(new Dsm(new ArrayList<DsmRow>()), new AnalysisResult(
					new HashMap<Dependency, Set<Violation>>(), new HashSet<Violation>(), true),
					null, null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("Title of DSM should not be empty", e.getMessage());
		}
		assertNotNull(ex);
	}


	@Test
	public void printNavigateDsmPackagesTest() throws MojoExecutionException {
		DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter("testDir");
		Exception ex = null;
		try {
			dsmHtmlWriter.printNavigateDsmPackages(null);
		} catch (IllegalArgumentException e) {
			ex = e;
			assertEquals("List of package names should not be null", e.getMessage());
		}
		assertNotNull(ex);
	}
}
