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

import org.apache.maven.plugin.MojoExecutionException;
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
	private final String classesFtl = "classes_page.ftl";
	private final String packagesFtl = "packages_page.ftl";
	private Dsm dsm;

	private DsmHtmlWriter dsmHtmlWriter;

	private String dsmReportSiteDirectory;

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
	 * @param aDsmDirectory
	 *            Namr of DSM report folder
	 */
	public void setDsmReportSiteDirectory(final String aDsmDirectory) {
		if (DsmHtmlWriter.isEmptyString(aDsmDirectory)) {
			throw new IllegalArgumentException("Dsm directory has no path.");
		}
		dsmReportSiteDirectory = aDsmDirectory + File.separator;
	}


	/**
	 * 
	 */
	public void startReport() throws MojoExecutionException {
		dsmHtmlWriter = new DsmHtmlWriter(dsmReportSiteDirectory);
		String[] arg = { "-input=" + sourceDirectory };
		startReport(arg);
	}


	/**
	 * Parse dependencies from target.
	 * 
	 * @param args
	 *            Arguments
	 */
	private void startReport(final String[] args) throws MojoExecutionException {
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
			final List<String> aPackageNames) throws MojoExecutionException {
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
	private void copySource() throws MojoExecutionException {
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
	 * @throws MojoExecutionException
	 */
	private void copyFileToSiteFolder(final String directoryOrFileName)
			throws MojoExecutionException {
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

			throw new MojoExecutionException("Can't find " + dsmReportSiteDirectory
					+ directoryOrFileName + " file. " + e.getMessage(), e);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to copy source file. " + e.getMessage(), e);
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				throw new MojoExecutionException("Can't close stream. " + e.getMessage(), e);
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
			final DependencyGraph aDependencyGraph, final String aPackageName)
			throws MojoExecutionException {
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
			final DependencyGraph aDependencyGraph, final String aAllPackages)
			throws MojoExecutionException {
		AnalysisResult analysisResult = getAnalysisResult(aArguments, aDependencies);
		dsmHtmlWriter.printDsm(new DsmEngine(aDependencyGraph).createDsm(), analysisResult,
				aAllPackages, packagesFtl);
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
			final AnalysisResult aAnalysisResult, final String aPackageName)
			throws MojoExecutionException {
		dsmHtmlWriter.printDsm(new DsmEngine(aDependencies).createDsm(), aAnalysisResult,
				aPackageName, classesFtl);
	}


	/**
	 * Print site navigation menu by packages
	 * 
	 * @param aPackageNames
	 *            List of package names
	 */
	private void printPackagesNavigationMenu(final List<String> aPackageNames)
			throws MojoExecutionException {
		dsmHtmlWriter.printNavigateDsmPackages(aPackageNames);
	}
}
