package org.sevntu.maven.plugin.dsm;

import java.util.ArrayList;
import java.util.List;

import org.hjug.dtangler.core.dependencies.Dependable;
import org.hjug.dtangler.core.dsm.Dsm;
import org.hjug.dtangler.core.dsm.DsmCell;
import org.hjug.dtangler.core.dsm.DsmRow;

/**
 * Changes the order of rows and columns of DSM.
 */
final class DsmSorter {

	/**
	 * Utility class, instances are not needed.
	 */
	private DsmSorter() {
	}

	/**
	 * Create DSM with the same content as the given one, but with rows and columns sorted by
	 * name. Dtangler keeps the cells of every row in the same order as the rows, so one new
	 * order is applied both to the rows and to the cells of each row. Cells are only moved,
	 * so the numbers and the cycles stay as they were. The given DSM is not changed.
	 *
	 * @param aDsm
	 *            Dsm structure
	 * @return new Dsm structure sorted by name
	 */
	static Dsm sortByName(final Dsm aDsm) {
		List<DsmRow> rows = aDsm.getRows();

		// position in the sorted DSM -> position in the given DSM
		List<Integer> order = new ArrayList<>(rows.size());
		for (int index = 0; index < rows.size(); index++) {
			order.add(index);
		}
		order.sort((first, second) -> compareByName(rows.get(first).getDependee(),
				rows.get(second).getDependee()));

		List<DsmRow> sortedRows = new ArrayList<>(rows.size());
		for (int rowIndex : order) {
			DsmRow row = rows.get(rowIndex);
			List<DsmCell> sortedCells = new ArrayList<>(order.size());
			for (int columnIndex : order) {
				sortedCells.add(row.getCells().get(columnIndex));
			}
			sortedRows.add(new DsmRow(row.getDependee(), sortedCells));
		}
		return new Dsm(sortedRows);
	}

	/**
	 * Compare packages or classes by the name that is shown in the report, ignoring case.
	 * Names that differ only by case, and equal names from different locations, are compared
	 * further to make the order always the same.
	 *
	 * @param aFirst
	 *            First package or class
	 * @param aSecond
	 *            Second package or class
	 * @return negative number, zero or positive number, as in Comparator
	 */
	private static int compareByName(final Dependable aFirst, final Dependable aSecond) {
		int result = aFirst.getDisplayName().compareToIgnoreCase(aSecond.getDisplayName());
		if (result == 0) {
			result = aFirst.getDisplayName().compareTo(aSecond.getDisplayName());
		}
		if (result == 0) {
			result = aFirst.getFullyQualifiedName().compareTo(aSecond.getFullyQualifiedName());
		}
		return result;
	}
}
