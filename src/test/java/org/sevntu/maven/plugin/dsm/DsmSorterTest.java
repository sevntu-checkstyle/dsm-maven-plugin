package org.sevntu.maven.plugin.dsm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hjug.dtangler.core.dependencies.Dependable;
import org.hjug.dtangler.core.dependencies.Dependency;
import org.hjug.dtangler.core.dsm.Dsm;
import org.hjug.dtangler.core.dsm.DsmCell;
import org.hjug.dtangler.core.dsm.DsmRow;
import org.hjug.dtangler.javaengine.types.JavaScope;
import org.junit.Test;


public class DsmSorterTest {

	@Test
	public void sortByNameTest() {
		// the same layout as in the report: row is used by column
		Dsm dsm = createDsm(
				createDependables("org.app.web", "org.app.service", "org.app.dao", "org.app.model"),
				new int[][] {
					{ 0, 0, 0, 0 },
					{ 5, 0, 0, 0 },
					{ 0, 3, 0, 0 },
					{ 1, 2, 4, 0 },
				});

		Dsm sortedDsm = DsmSorter.sortByName(dsm);

		assertEquals(Arrays.asList("org.app.dao", "org.app.model", "org.app.service", "org.app.web"),
				getNames(sortedDsm));
		assertArrayEquals(new int[][] {
				{ 0, 0, 3, 0 },
				{ 4, 0, 2, 1 },
				{ 0, 0, 0, 5 },
				{ 0, 0, 0, 0 },
			}, getWeights(sortedDsm));
		assertCellsFollowColumns(sortedDsm);
	}

	@Test
	public void sortByNameDoesNotChangeGivenDsmTest() {
		int[][] weights = new int[][] {
			{ 0, 0 },
			{ 7, 0 },
		};
		Dsm dsm = createDsm(createDependables("org.app.web", "org.app.dao"), weights);

		Dsm sortedDsm = DsmSorter.sortByName(dsm);

		assertNotSame(dsm, sortedDsm);
		assertEquals(Arrays.asList("org.app.web", "org.app.dao"), getNames(dsm));
		assertArrayEquals(weights, getWeights(dsm));
	}

	@Test
	public void sortByNameIgnoresCaseTest() {
		assertEquals(Arrays.asList("apple", "Mango", "Zebra"),
				getNames(DsmSorter.sortByName(createDsm(createDependables("Zebra", "apple", "Mango")))));

		// names that differ only by case have the same order for any order of input
		assertEquals(Arrays.asList("ABC", "Abc"),
				getNames(DsmSorter.sortByName(createDsm(createDependables("Abc", "ABC")))));
		assertEquals(Arrays.asList("ABC", "Abc"),
				getNames(DsmSorter.sortByName(createDsm(createDependables("ABC", "Abc")))));

		// name is compared before the location
		Dependable upperCase = new Dependable(JavaScope.classes, "b.jar: org.app.ABC", "ABC", 1);
		Dependable mixedCase = new Dependable(JavaScope.classes, "a.jar: org.app.Abc", "Abc", 1);
		assertEquals(Arrays.asList(upperCase, mixedCase),
				getDependables(DsmSorter.sortByName(createDsm(mixedCase, upperCase))));
	}

	@Test
	public void sortByNamePackagesWithSamePrefixTest() {
		Dsm dsm = createDsm(createDependables("org.app.webapp", "default", "org.app.web.util",
				"org.app", "org.app.web"));

		assertEquals(Arrays.asList("default", "org.app", "org.app.web", "org.app.web.util",
				"org.app.webapp"), getNames(DsmSorter.sortByName(dsm)));
	}

	@Test
	public void sortByNameSameNamesTest() {
		Dependable first = new Dependable(JavaScope.classes, "a.jar: org.app.Util", "Util", 1);
		Dependable second = new Dependable(JavaScope.classes, "b.jar: org.app.Util", "Util", 1);

		assertEquals(Arrays.asList(first, second),
				getDependables(DsmSorter.sortByName(createDsm(second, first))));
		assertEquals(Arrays.asList(first, second),
				getDependables(DsmSorter.sortByName(createDsm(first, second))));
	}

	@Test
	public void sortByNameEmptyDsmTest() {
		Dsm sortedDsm = DsmSorter.sortByName(new Dsm(new ArrayList<DsmRow>()));

		assertTrue(sortedDsm.getRows().isEmpty());
	}

	private static Dependable[] createDependables(String... aNames) {
		Dependable[] dependables = new Dependable[aNames.length];
		for (int index = 0; index < aNames.length; index++) {
			dependables[index] = new Dependable(JavaScope.packages, "location: " + aNames[index],
					aNames[index], 1);
		}
		return dependables;
	}

	private static Dsm createDsm(Dependable... aDependables) {
		return createDsm(aDependables, new int[aDependables.length][aDependables.length]);
	}

	private static Dsm createDsm(Dependable[] aDependables, int[][] aWeights) {
		List<DsmRow> rows = new ArrayList<DsmRow>();
		for (int row = 0; row < aDependables.length; row++) {
			List<DsmCell> cells = new ArrayList<DsmCell>();
			for (int column = 0; column < aDependables.length; column++) {
				cells.add(new DsmCell(aDependables[column], aDependables[row], aWeights[row][column]));
			}
			rows.add(new DsmRow(aDependables[row], cells));
		}
		return new Dsm(rows);
	}

	private static List<Dependable> getDependables(Dsm aDsm) {
		List<Dependable> dependables = new ArrayList<Dependable>();
		for (DsmRow row : aDsm.getRows()) {
			dependables.add(row.getDependee());
		}
		return dependables;
	}

	private static List<String> getNames(Dsm aDsm) {
		List<String> names = new ArrayList<String>();
		for (DsmRow row : aDsm.getRows()) {
			names.add(row.getDependee().getDisplayName());
		}
		return names;
	}

	private static int[][] getWeights(Dsm aDsm) {
		int[][] weights = new int[aDsm.getRows().size()][];
		for (int row = 0; row < weights.length; row++) {
			List<DsmCell> cells = aDsm.getRows().get(row).getCells();
			weights[row] = new int[cells.size()];
			for (int column = 0; column < cells.size(); column++) {
				weights[row][column] = cells.get(column).getDependencyWeight();
			}
		}
		return weights;
	}

	/**
	 * Every cell has to describe how the item of its column uses the item of its row,
	 * and only the cells on the diagonal are not valid.
	 */
	private static void assertCellsFollowColumns(Dsm aDsm) {
		List<Dependable> dependables = getDependables(aDsm);
		for (int row = 0; row < dependables.size(); row++) {
			List<DsmCell> cells = aDsm.getRows().get(row).getCells();
			assertEquals(dependables.size(), cells.size());
			for (int column = 0; column < cells.size(); column++) {
				assertEquals(new Dependency(dependables.get(column), dependables.get(row)),
						cells.get(column).getDependency());
				assertEquals(row != column, cells.get(column).isValid());
			}
		}
	}
}
