package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;


public class DsmRowModelTest {

	@Test
	public void dsmRowModelTest() {
		List<String> numberOfDependencies = new ArrayList<String>();
		numberOfDependencies.add("1");
		numberOfDependencies.add("2");
		DsmRowModel dsmRowData = new DsmRowModel(1, "row name", "", numberOfDependencies);

		assertTrue("row name".equals(dsmRowData.getName()));
		assertTrue("1".equals(dsmRowData.getNumberOfDependencies().get(0)));
		assertTrue("2".equals(dsmRowData.getNumberOfDependencies().get(1)));
	}


	@Test
	public void testObfuscation() {
		DsmRowModel rowModel = new DsmRowModel(1, "com.puppycrawl.tools.checkstyle.checks.coding",
				"c.p.t.c.c.coding", Collections.EMPTY_LIST);
		assertEquals("c.p.t.c.c.coding", rowModel.getObfuscatedPackageName());
	}


	@Test
	public void testCutNames() {
		DsmRowModel rowModel = new DsmRowModel(1, "com.puppycrawl.tools.checkstyle.checks.coding",
				"...puppycrawl.tools.checkstyle.checks.coding", Collections.EMPTY_LIST);
		assertEquals("...puppycrawl.tools.checkstyle.checks.coding",
				rowModel.getObfuscatedPackageName());
	}
}
