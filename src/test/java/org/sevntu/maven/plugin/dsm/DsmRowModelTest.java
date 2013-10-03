package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class DsmRowModelTest {

	@Test
	public void dsmRowModelTest() {
		List<String> numberOfDependencies = new ArrayList<String>();
		numberOfDependencies.add("1");
		numberOfDependencies.add("2");
		DsmRowModel dsmRowData = new DsmRowModel(1, "row name", 2, numberOfDependencies);

		assertTrue(dsmRowData.getnumberOfClasses() == 2);
		assertTrue("row name".equals(dsmRowData.getName()));
		assertTrue("1".equals(dsmRowData.getNumberOfDependencies().get(0)));
		assertTrue("2".equals(dsmRowData.getNumberOfDependencies().get(1)));
	}
}
