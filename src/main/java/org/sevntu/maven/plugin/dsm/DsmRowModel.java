package org.sevntu.maven.plugin.dsm;

import java.util.List;


public class DsmRowModel implements Comparable<DsmRowModel> {

	/**
	 * Package or class name
	 */
	private String name;

	/**
	 * List of dependencies. Contain number of dependencies with other
	 * classes/packages
	 */
	private List<String> numberOfDependencies;

	/**
	 * Number of classes contained in the package
	 */
	private int numberOfClasses;

	/**
	 * Package or class index in dsm
	 */
	private int positionIndex;


	public DsmRowModel(String aName, int aContentCount, List<String> aNumberOfDependencies) {
		name = aName;
		numberOfClasses = aContentCount;
		numberOfDependencies = aNumberOfDependencies;
	}


	public DsmRowModel(int positionIndex, String aName, int aContentCount,
			List<String> aNumberOfDependencies) {
		this(aName, aContentCount, aNumberOfDependencies);
		this.positionIndex = positionIndex;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * 
	 * @return truncated name
	 */
	public String getTruncatedName() {
		return formatName(name, 40);
	}


	/**
	 * @return the numberOfDependencies
	 */
	public List<String> getNumberOfDependencies() {
		return numberOfDependencies;
	}


	/**
	 * @return number of classes contained in the package
	 */
	public int getnumberOfClasses() {
		return numberOfClasses;
	}


	/**
	 * @return the positionIndex
	 */
	public int getPositionIndex() {
		return positionIndex;
	}


	/**
	 * 
	 * @param positionIndex
	 */
	public void setPositionIndex(int positionIndex) {
		this.positionIndex = positionIndex;
	}


	/**
	 * Truncate package or class name
	 * 
	 * @param aName
	 *            Package or class name
	 * @param aLength
	 *            Maximum length of name
	 * @return Truncated name
	 */
	public String formatName(final String aName, final int aLength) {
		if (aName.length() - 2 <= aLength) {
			return aName;
		}
		return ".." + aName.substring(aName.length() - aLength - 2);
	}


	@Override
	public int compareTo(DsmRowModel o) {
		return name.compareTo(o.name);
	}
}
