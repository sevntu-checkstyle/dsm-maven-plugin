package org.sevntu.maven.plugin.dsm;

import java.util.List;


public class DsmRowData {

	private String name;
	private List<String> numberOfDependencies;
	private int dependencyContentCount;
	private int positionIndex;


	public DsmRowData(int positionIndex, String name, int dependencyContentCount,
			List<String> numberOfDependencies) {
		this.positionIndex = positionIndex;
		this.name = name;
		this.dependencyContentCount = dependencyContentCount;
		this.numberOfDependencies = numberOfDependencies;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return the numberOfDependencies
	 */
	public List<String> getNumberOfDependencies() {
		return numberOfDependencies;
	}


	/**
	 * @return the depCount
	 */
	public int getDependencyContentCount() {
		return dependencyContentCount;
	}


	/**
	 * @return the positionIndex
	 */
	public int getPositionIndex() {
		return positionIndex;
	}
}
