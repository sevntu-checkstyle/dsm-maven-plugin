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
import org.dtangler.core.dependencies.DependencyGraph;
import org.dtangler.core.dependencies.Scope;
import org.dtangler.core.dependencyengine.DependencyEngine;
import org.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.dtangler.core.dsm.Dsm;
import org.dtangler.core.dsm.DsmRow;
import org.dtangler.core.dsmengine.DsmEngine;
import org.dtangler.core.input.ArgumentBuilder;


/**
 * This class begins dependency analysis of input files
 * 
 * @author Yuri Balakhonov
 * 
 */
public class DsmReport {

	private final String imagesFolderName = "images";
	private final String cssFolderName = "css";

	private Dsm dsm;

	private DsmHtmlWriter dsmHtmlWriter;

	private String dsmReportSiteDirectory;

	private String outputDirectory;


	/**
	 * 
	 * @param aOutputDirectory
	 *            Full output directory path
	 */
	public void setOutputDirectory(final String aOutputDirectory) {
		if (!textHasContent(aOutputDirectory)) {
			throw new IllegalArgumentException("Output directory has no path.");
		}
		outputDirectory = aOutputDirectory;
	}


	/**
	 * 
	 * @param aDsmDirectory
	 *            Namr of DSM report folder
	 */
	public void setDsmReportSiteDirectory(final String aDsmDirectory) {
		if (!textHasContent(aDsmDirectory)) {
			throw new IllegalArgumentException("Dsm directory has no path.");
		}
		dsmReportSiteDirectory = aDsmDirectory + File.separator;
	}


	/**
	 * 
	 */
	public void startReport() {
		dsmHtmlWriter = new DsmHtmlWriter(dsmReportSiteDirectory);
		String[] arg = { "-input=" + outputDirectory };
		startReport(arg);
	}


	/**
	 * Parse dependencies from target.
	 * 
	 * @param args
	 *            Arguments
	 */
	private void startReport(final String[] args) {
		Arguments arguments = new ArgumentBuilder().build(args);
		DependencyEngine engine = new DependencyEngineFactory().getDependencyEngine(arguments);

		Dependencies dependencies = engine.getDependencies(arguments);
		DependencyGraph dependencyGraph = dependencies.getDependencyGraph();

		dsm = new DsmEngine(dependencyGraph).createDsm();

		List<String> packageNames = getPackageNames(dsm);

		printPackagesNavigationMenu(packageNames);

		printDsmForPackages(dependencies, arguments, dependencyGraph, "all_packages");

		printDsmForClasses(engine, arguments, packageNames);

		copySource();
	}


	/**
	 * Print DSM for all packages.
	 * 
	 * @param aEngine
	 *            DependencyEngine
	 * @param aArguments
	 *            Input arguments
	 * @param aPackageNames
	 *            List of the package names
	 */
	private void printDsmForClasses(final DependencyEngine aEngine, final Arguments aArguments,
			final List<String> aPackageNames) {
		for (int packageIndex = 0; packageIndex < dsm.getRows().size(); packageIndex++) {
			Dependencies dependencies2 = aEngine.getDependencies(aArguments);
			Scope scope = dependencies2.getChildScope(dependencies2.getDefaultScope());
			Set<Dependable> dep = getDependablesByRowIndex(packageIndex);

			DependencyGraph dependencyGraph2 = dependencies2.getDependencyGraph(scope, dep,
					Dependencies.DependencyFilter.none);

			analisisAndPrintDsm(dependencies2, aArguments, dependencyGraph2,
					aPackageNames.get(packageIndex));
		}
	}


	/**
	 * Move sourc files from project source folder to the site folder.
	 */
	private void copySource() {
		createTheDirectories();
		copyFileToSiteFolder("index.html");
		copyFileToSiteFolder(cssFolderName + File.separator + "style.css");
		copyFileToSiteFolder(imagesFolderName + File.separator + "class.png");
		copyFileToSiteFolder(imagesFolderName + File.separator + "package.png");
		copyFileToSiteFolder(imagesFolderName + File.separator + "packages.png");
	}


	/**
	 * Create the folders in a site directory.
	 */
	private void createTheDirectories() {
		String[] pluginDirectories = { imagesFolderName, cssFolderName };
		for (String dir : pluginDirectories) {
			File outputFile = new File(dsmReportSiteDirectory + dir);
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
			File outputFile = new File(dsmReportSiteDirectory + directoryOrFileName);
			inputStream = getClass().getResourceAsStream(File.separator + directoryOrFileName);
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
	private void analisisAndPrintDsm(final Dependencies aDependencies, final Arguments aArguments,
			final DependencyGraph aDependencyGraph, final String aPackageName) {
		AnalysisResult analysisResult = getAnalysisResult(aArguments, aDependencies);
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
	private void printDsmForPackages(final Dependencies aDependencies, final Arguments aArguments,
			final DependencyGraph aDependencyGraph, final String aAllPackages) {
		AnalysisResult analysisResult = getAnalysisResult(aArguments, aDependencies);
		dsmHtmlWriter.printDsmPackages(new DsmEngine(aDependencyGraph).createDsm(), analysisResult,
				aAllPackages);
	}


	/**
	 * Get Set of dependables.
	 * 
	 * @param aRow
	 *            Index of row wich analysing
	 * @return Set of Dependables
	 */
	private Set<Dependable> getDependablesByRowIndex(final int aRow) {
		Set<Dependable> result = new HashSet<Dependable>();
		result.add(dsm.getRows().get(aRow).getDependee());
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
		dsmHtmlWriter.printDsm(new DsmEngine(aDependencies).createDsm(), aAnalysisResult,
				aPackageName);
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


	private boolean textHasContent(String aText) {
		return (aText != null) && (!aText.trim().isEmpty());
	}
}
