package com.sevntu.maven.plugin.dsm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.dtangler.core.analysisresult.AnalysisResult;
import org.dtangler.core.configuration.Arguments;
import org.dtangler.core.dependencies.Dependencies;
import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.Dependency;
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmCell;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.input.ArgumentBuilder;

/**
 * This class begins dependency analysis of input files
 * 
 * @author Yuri Balakhonov
 * 
 */
public class Main {
	private Dsm dsm;

	private DsmHtmlWriter dsmHtmlWriter = new DsmHtmlWriter();

	private String dsmReportSiteDirectory;

	private String outputDirectory;

	public void setOutputDirectory(final String aOutputDirectory) {
		outputDirectory = aOutputDirectory;
	}

	public void setDSMReportSiteDirectory(final String aDSMDirectory) {
		dsmReportSiteDirectory = "/site/" + aDSMDirectory + "/";
	}

	public void startReport() {
		String[] arg = { "-input=" + outputDirectory + "/classes" };
		startReport(arg);
		copySource();
	}

	/**
	 * Parse dependencies from target.
	 * 
	 * @param args
	 *            Arguments
	 */
	private void startReport(final String[] args) {
		List<String> packageNames = new ArrayList<String>();

		Arguments arguments = new ArgumentBuilder().build(args);
		DependencyEngine engine = new DependencyEngineFactory()
				.getDependencyEngine(arguments);

		Dependencies dependencies = engine.getDependencies(arguments);
		DependencyGraph dependencyGraph = dependencies.getDependencyGraph();

		dsm = new DsmEngine(dependencyGraph).createDsm();
		packageNames = getPackageNames(dsm);

		printPackagesNavigationMenu(packageNames);
		analisisAndPrintDsmPackages(dependencies, arguments, dependencyGraph,
				"all_packages");

		for (int packageIndex = 0; packageIndex < dsm.getRows().size(); packageIndex++) {
			Dependencies dependencies2 = engine.getDependencies(arguments);
			Scope scope = dependencies2.getChildScope(dependencies2
					.getDefaultScope());
			Set<Dependable> dep = getSelectionDependables(packageIndex);

			DependencyGraph dependencyGraph2 = dependencies2
					.getDependencyGraph(scope, dep,
							Dependencies.DependencyFilter.none);

			analisisAndPrintDsm(dependencies2, arguments, dependencyGraph2,
					packageNames.get(packageIndex));
		}
	}

	/**
	 * Move sourc files from project source folder to the site folder.
	 */
	private void copySource() {
		createTheDirectories();
		copyFileToSiteFolder("index.html");
		copyFileToSiteFolder("css/style.css");
		copyFileToSiteFolder("images/class.png");
		copyFileToSiteFolder("images/package.png");
		copyFileToSiteFolder("images/packages.png");
	}

	/**
	 * Create the folders in a site directory.
	 */
	private void createTheDirectories() {
		String[] pluginDirectories = { "images", "css" };
		for (String dir : pluginDirectories) {
			File outputFile = new File(outputDirectory + dsmReportSiteDirectory
					+ dir);
			if (!outputFile.exists()) {
				outputFile.mkdir();
			}

		}
	}

	/**
	 * Copy file to the site directory
	 * 
	 * @param directoryOrFileName
	 *            Directory or File name
	 */
	private void copyFileToSiteFolder(final String directoryOrFileName) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			int numberOfBytes;
			byte[] buffer = new byte[1024];
			File outputFile = new File(outputDirectory + dsmReportSiteDirectory
					+ directoryOrFileName);
			inputStream = getClass().getResourceAsStream(
					"/" + directoryOrFileName);
			outputStream = new FileOutputStream(outputFile);

			while ((numberOfBytes = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, numberOfBytes);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param aDsm
	 *            DSM structure
	 * @return List of Package Names
	 */
	private List<String> getPackageNames(final Dsm aDsm) {
		List<DsmRow> dsmRowList = aDsm.getRows();
		List<String> packageNames = new ArrayList<String>();
		for (int index = 0; index < dsmRowList.size(); index++) {
			DsmRow dsmRow = dsmRowList.get(index);
			packageNames.add(dsmRow.getDependee().toString());
		}
		return packageNames;
	}

	/**
	 * Analysin and print DSM of Class from package
	 * 
	 * @param aDependencies
	 *            Package dependensies
	 * @param aArguments
	 *            Arguments
	 * @param aDependencyGraph
	 *            Dependency graph
	 * @param aPackageName
	 *            Package name
	 */
	public void analisisAndPrintDsm(final Dependencies aDependencies,
			final Arguments aArguments, final DependencyGraph aDependencyGraph,
			final String aPackageName) {
		AnalysisResult analysisResult = getAnalysisResult(aArguments,
				aDependencies);
		printDsm(aDependencyGraph, analysisResult, aPackageName);
	}

	/**
	 * Analysin and print DSM of package from project
	 * 
	 * @param aDependencies
	 *            Project dependensies
	 * @param aArguments
	 *            Arguments
	 * @param aDependencyGraph
	 *            Dependency graph
	 * @param aAllPackages
	 *            "all_packages"
	 */
	public void analisisAndPrintDsmPackages(final Dependencies aDependencies,
			final Arguments aArguments, final DependencyGraph aDependencyGraph,
			final String aAllPackages) {
		AnalysisResult analysisResult = getAnalysisResult(aArguments,
				aDependencies);
		dsmHtmlWriter.printDsmPackages(
				new DsmEngine(aDependencyGraph).createDsm(), analysisResult,
				aAllPackages);
	}

	/**
	 * Get Set of dependencies.
	 * 
	 * @param aRow
	 *            Index of row wich analysing
	 * @return Set of Dependencies
	 */
	public Set<Dependency> getSelectionDependencies(final int aRow) {
		Set<Dependency> result = new HashSet<Dependency>();
		result.add(getDsmCell(0, 0).getDependency());
		return result;
	}

	/**
	 * Get Set of dependables.
	 * 
	 * @param aRow
	 *            Index of row wich analysing
	 * @return Set of Dependables
	 */
	public Set<Dependable> getSelectionDependables(final int aRow) {
		Set<Dependency> selectionDependencies = new HashSet<Dependency>();
		Set<Dependable> result = new HashSet<Dependable>();
		if (selectionDependencies.isEmpty()) {
			result.add(dsm.getRows().get(aRow).getDependee());
		} else {
			for (Dependency dependency : selectionDependencies) {
				result.add(dependency.getDependant());
				result.add(dependency.getDependee());
			}
		}
		return result;
	}

	/**
	 * Get DSMCell by row and coll indexes
	 * 
	 * @param aRow
	 *            Row index
	 * @param aCol
	 *            Col index
	 * @return DSMCell structure
	 */
	private DsmCell getDsmCell(final int aRow, final int aCol) {
		return dsm.getRows().get(aRow).getCells().get(aCol);
	}

	/**
	 * Analisyng dependencies by arguments
	 * 
	 * @param arguments
	 *            Arguments
	 * @param dependencies
	 *            Dependencies structure
	 * @return AnalysisResult structure
	 */
	private AnalysisResult getAnalysisResult(final Arguments arguments,
			final Dependencies dependencies) {
		return new ConfigurableDependencyAnalyzer(arguments)
				.analyze(dependencies);
	}

	/**
	 * Print DSM
	 * 
	 * @param aDependencies
	 *            Dependencies structure
	 * @param aAnalysisResult
	 *            AnalysisResult structure
	 * @param aPackageName
	 *            Package name
	 */
	private void printDsm(final DependencyGraph aDependencies,
			final AnalysisResult aAnalysisResult, final String aPackageName) {
		dsmHtmlWriter.printDsm(new DsmEngine(aDependencies).createDsm(),
				aAnalysisResult, aPackageName);
	}

	/**
	 * Print site navigation menu by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	private void printPackagesNavigationMenu(final List<String> aPackageNames) {
		dsmHtmlWriter.printNavigateDsmPackages(aPackageNames);
	}
}
