package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.analysisresult.Violation;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.javaengine.types.JavaScope;
import org.junit.Test;


public class DsmHtmlWriterTest {

	@Test
	public void dsmHtmlWriterTest() {
		try {
			new DsmHtmlWriter(null, false);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("Path to the report directory should not be null or empty", e.getMessage());
		}

		try {
			new DsmHtmlWriter("", false);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("Path to the report directory should not be null or empty", e.getMessage());
		}
	}


	@Test
	public void printDsmTest() {
		DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter("target/testDir", false);
		Dsm dsm = new Dsm(new ArrayList<DsmRow>());
		AnalysisResult ar = new AnalysisResult(new HashMap<Dependency, Set<Violation>>(),
				new HashSet<Violation>(), true);
		Scope scope = JavaScope.packages;
		String title = "Title";

		try {
			dsmHtmlWriter.printDsm(null, null, null, null, null);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("DSM structure should not be null", e.getMessage());
		}

		try {
			dsmHtmlWriter.printDsm(dsm, null, null, null, null);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("Analysis structure should not be null", e.getMessage());
		}

		try {
			dsmHtmlWriter.printDsm(dsm, ar, null, null, null);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("Scope should not be null", e.getMessage());
		}

		try {
			dsmHtmlWriter.printDsm(dsm, ar, scope, null, null);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("Title of DSM should not be empty", e.getMessage());
		}

		try {
			dsmHtmlWriter.printDsm(dsm, ar, scope, title, null);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("Template name should not be empty", e.getMessage());
		}
	}


	@Test
	public void printNavigateDsmPackagesTest() {
		DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter("target/testDir", false);
		try {
			dsmHtmlWriter.printDsmPackagesNavigation(null);
			Assert.fail();
		} catch (Exception e) {
			assertEquals("List of package names should not be null", e.getMessage());
		}
	}
}
