package org.sevntu.maven.plugin.dsm;

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
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsmengine.DsmEngine;


/**
 * This class begins dependency analysis of input files
 * 
 * @author Yuri Balakhonov
 * 
 */
public class DsmReport {

	private DsmHtmlWriter dsmHtmlWriter;

	/**
	 * The output directory for the report.
	 */
	private String outputDirectory;

	/**
	 * The java source directory
	 */
	private String sourceDirectory;


	/**
	 * 
	 * @param aSourceDirectory
	 *            Full output directory path
	 */
	public void setSourceDirectory(final String aSourceDirectory) {
		if (DsmHtmlWriter.isEmptyString(aSourceDirectory)) {
			throw new IllegalArgumentException("Source directory has no path.");
		}
		sourceDirectory = aSourceDirectory;
	}


	/**
	 * 
	 * @param aOutputDirectory
	 *            Name of DSM report folder
	 */
	public void setOutputDirectory(final String aOutputDirectory) {
		if (DsmHtmlWriter.isEmptyString(aOutputDirectory)) {
			throw new IllegalArgumentException("Dsm directory has no path.");
		}
		outputDirectory = aOutputDirectory + File.separator;
	}


	/**
	 * 
	 * @throws Exception
	 */
	public void startReport() throws Exception {
		dsmHtmlWriter = new DsmHtmlWriter(outputDirectory);

		List<String> sourcePathList = new ArrayList<String>();
		sourcePathList.add(sourceDirectory);

		startReport(sourcePathList);
	}


	/**
	 * Parse dependencies from target.
	 * 
	 * @param aSourcePathList
	 *            path list of project source
	 * @throws Exception
	 */
	private void startReport(final List<String> aSourcePathList) throws Exception {
		Arguments arguments = new Arguments();
		arguments.setInput(aSourcePathList);

		DependencyEngine engine = new DependencyEngineFactory().getDependencyEngine(arguments);
		Dependencies dependencies = engine.getDependencies(arguments);
		DependencyGraph dependencyGraph = dependencies.getDependencyGraph();
		AnalysisResult analysisResult = getAnalysisResult(arguments, dependencies);
		Dsm dsm = new DsmEngine(dependencyGraph).createDsm();

		// print dsm site navigation by packages
		List<String> packageNames = getPackageNames(dsm);
		dsmHtmlWriter.printNavigateDsmPackages(packageNames);

		printDsmForPackages(dependencies, analysisResult, dependencyGraph, "all_packages");
		printDsmForClasses(dsm, dependencies, analysisResult, packageNames);

		copySource();
	}


	/**
	 * 
	 * Print DSM for for each package in project.
	 * 
	 * @param aDsm
	 *            Dsm
	 * @param aDependencies
	 *            Dependencies
	 * @param aAnalysisResult
	 *            Analysis result
	 * @param aPackageNames
	 *            List of the package names
	 * @throws Exception
	 */
	private void printDsmForClasses(final Dsm aDsm, final Dependencies aDependencies,
			final AnalysisResult aAnalysisResult, final List<String> aPackageNames)
			throws Exception {
		Scope scope = aDependencies.getChildScope(aDependencies.getDefaultScope());

		for (int packageIndex = 0; packageIndex < aDsm.getRows().size(); packageIndex++) {
			Set<Dependable> ependableSet = getDependablesByRowIndex(aDsm, packageIndex);

			DependencyGraph dependencyGraph = aDependencies.getDependencyGraph(scope, ependableSet,
					Dependencies.DependencyFilter.none);

			Dsm dsm = new DsmEngine(dependencyGraph).createDsm();

			dsmHtmlWriter.printDsm(dsm, aAnalysisResult, aPackageNames.get(packageIndex),
					DsmHtmlWriter.FTL_CLASSES_PAGE);
		}
	}


	/**
	 * Get name list of project packages
	 * 
	 * @param aDsm
	 *            DSM structure
	 * @return List of package names
	 */
	private List<String> getPackageNames(final Dsm aDsm) {
		List<DsmRow> dsmRowList = aDsm.getRows();
		List<String> packageNames = new ArrayList<String>();
		for (DsmRow dsmRow : dsmRowList) {
			packageNames.add(dsmRow.getDependee().toString());
		}
		return packageNames;
	}


	/**
	 * Print dsm for packages
	 * 
	 * @param aDependencies
	 *            Project dependensies
	 * @param aAnalysisResult
	 *            Analysis result
	 * @param aDependencyGraph
	 *            Dependency graph
	 * @param aAllPackages
	 *            "all_packages"
	 */
	private void printDsmForPackages(final Dependencies aDependencies,
			final AnalysisResult aAnalysisResult, final DependencyGraph aDependencyGraph,
			final String aAllPackages) throws Exception {
		Dsm dsm = new DsmEngine(aDependencyGraph).createDsm();
		dsmHtmlWriter.printDsm(dsm, aAnalysisResult, aAllPackages, DsmHtmlWriter.FTL_PACKAGES_PAGE);
	}


	/**
	 * Get Set of dependables.
	 * 
	 * @param aDsm
	 *            Dsm
	 * @param aRow
	 *            Index of row wich analysing
	 * @return Set of Dependables
	 */
	private Set<Dependable> getDependablesByRowIndex(final Dsm aDsm, final int aRow) {
		Set<Dependable> result = new HashSet<Dependable>();
		result.add(aDsm.getRows().get(aRow).getDependee());
		return result;
	}


	/**
	 * Analisyng dependencies by arguments
	 * 
	 * @param aArguments
	 *            Arguments
	 * @param aDependencies
	 *            Dependencies structure
	 * @return AnalysisResult structure
	 */
	private AnalysisResult getAnalysisResult(final Arguments aArguments,
			final Dependencies aDependencies) {
		return new ConfigurableDependencyAnalyzer(aArguments).analyze(aDependencies);
	}


	/**
	 * Move source files from project source folder to the site folder.
	 * 
	 * @throws Exception
	 */
	private void copySource() throws Exception {
		copyFileToSiteFolder(outputDirectory, "", "index.html");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.CSS_FOLDER_NAME, "style.css");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.IMAGE_FOLDER_NAME, "class.png");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.IMAGE_FOLDER_NAME, "package.png");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.IMAGE_FOLDER_NAME, "packages.png");
	}


	/**
	 * Create the folder in a site directory.
	 * 
	 * @param aOutputDirectory
	 *            Report output directory
	 * @param aDirName
	 *            Folder name
	 */
	private static void makeDirectory(String aOutputDirectory, String aDirName) {
		new File(aOutputDirectory + aDirName).mkdirs();
	}


	/**
	 * Copy file to the site directory
	 * 
	 * @param aOutputDirectory
	 *            Report output directory
	 * @param aDirName
	 *            Directory name
	 * @param aFileName
	 *            File name
	 * @throws Exception
	 */
	private static void copyFileToSiteFolder(final String aOutputDirectory, final String aDirName,
			final String aFileName) throws Exception {
		String fileName;

		// check directory
		if (aDirName != null && !aDirName.isEmpty()) {
			makeDirectory(aOutputDirectory, aDirName);
			fileName = aDirName + File.separator + aFileName;
		} else {
			fileName = aFileName;
		}

		copyFileToSiteFolder(aOutputDirectory, fileName);
	}


	/**
	 * Copy file to the site directory
	 * 
	 * @param aOutputDirectory
	 *            Report output directory
	 * @param aFileName
	 *            File name
	 * @throws Exception
	 */
	private static void copyFileToSiteFolder(final String aOutputDirectory, final String aFileName)
			throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			int numberOfBytes;
			byte[] buffer = new byte[1024];

			outputStream = new FileOutputStream(new File(aOutputDirectory + aFileName));
			inputStream = DsmReport.class.getResourceAsStream(File.separator + aFileName);

			while ((numberOfBytes = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, numberOfBytes);
			}
		} catch (FileNotFoundException e) {
			throw new Exception("Can't find " + aOutputDirectory + aFileName + " file. "
					+ e.getMessage(), e);
		} catch (IOException e) {
			throw new Exception("Unable to copy source file. " + e.getMessage(), e);
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				throw new Exception("Can't close stream. " + e.getMessage(), e);
			}
		}
	}
}
