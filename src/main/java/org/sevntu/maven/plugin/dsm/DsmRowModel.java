package org.sevntu.maven.plugin.dsm;

import java.util.List;

/**
 * 
 * @author Yuri Balakhonov
 * @author Ilja Dubinin
 */
public class DsmRowModel {

	/**
	 * Package or class name
	 */
	private String name;

	private String obfuscatedPackageName;

	/**
	 * List of dependencies. Contain number of dependencies with other classes/packages
	 */
	private List<String> numberOfDependencies;

	/**
	 * Package or class index in dsm
	 */
	private int positionIndex;

	public DsmRowModel(int aPositionIndex, String aName, String obfuscatedPackageName,
			List<String> aNumberOfDependencies) {
		positionIndex = aPositionIndex;
		name = aName;
		this.obfuscatedPackageName = obfuscatedPackageName;
		numberOfDependencies = aNumberOfDependencies;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return truncated name
	 */
	public String getObfuscatedPackageName() {
		return obfuscatedPackageName;
	}

	/**
	 * @return the numberOfDependencies
	 */
	public List<String> getNumberOfDependencies() {
		return numberOfDependencies;
	}

	/**
	 * @return the positionIndex
	 */
	public int getPositionIndex() {
		return positionIndex;
	}

}
