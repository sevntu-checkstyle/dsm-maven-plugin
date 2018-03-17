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

import org.apache.maven.reporting.MavenReportException;
import org.hjug.dtangler.core.analysis.configurableanalyzer.ConfigurableDependencyAnalyzer;
import org.hjug.dtangler.core.analysisresult.AnalysisResult;
import org.hjug.dtangler.core.configuration.Arguments;
import org.hjug.dtangler.core.dependencies.Dependable;
import org.hjug.dtangler.core.dependencies.Dependencies;
import org.hjug.dtangler.core.dependencies.DependencyGraph;
import org.hjug.dtangler.core.dependencies.Scope;
import org.hjug.dtangler.core.dependencyengine.DependencyEngine;
import org.hjug.dtangler.core.dependencyengine.DependencyEngineFactory;
import org.hjug.dtangler.core.dsm.Dsm;
import org.hjug.dtangler.core.dsm.DsmRow;
import org.hjug.dtangler.core.dsmengine.DsmEngine;

import com.google.common.base.Strings;

/**
 * This class begins dependency analysis of input files
 * 
 * @author Yuri Balakhonov
 * @author Ilja Dubinin
 */
public class DsmReportEngine {

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
	 * Obfuscate packages names.
	 */
	private boolean obfuscatePackageNames;

	/**
	 * 
	 * @param aSourceDirectory
	 *            Full output directory path
	 * @throws MavenReportException 
	 */
	public void setSourceDirectory(final String aSourceDirectory) throws IllegalArgumentException {
		if (Strings.isNullOrEmpty(aSourceDirectory)) {
			throw new IllegalArgumentException("Source directory path can't be empty.");
		}
		if (!new File(aSourceDirectory).exists()) {
			throw new IllegalArgumentException("Source directory '" + aSourceDirectory + "' not exists");
		}
		sourceDirectory = aSourceDirectory;
	}

	/**
	 * 
	 * @param aOutputDirectory
	 *            Name of DSM report folder
	 */
	public void setOutputDirectory(final String aOutputDirectory) {
		if (Strings.isNullOrEmpty(aOutputDirectory)) {
			throw new IllegalArgumentException("Dsm directory is empty.");
		}
		outputDirectory = aOutputDirectory + File.separator;
	}

	/**
	 * @param obfuscate
	 */
	public void setObfuscatePackageNames(boolean obfuscate) {
		this.obfuscatePackageNames = obfuscate;
	}

	/**
	 * Get name list of project packages
	 * 
	 * @param aDsm
	 *            DSM structure
	 * @return List of package names
	 */
	private static List<String> getPackageNames(final Dsm aDsm) {
		List<DsmRow> dsmRowList = aDsm.getRows();
		List<String> packageNames = new ArrayList<>();
		for (DsmRow dsmRow : dsmRowList) {
			packageNames.add(dsmRow.getDependee().toString());
		}
		return packageNames;
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
	private static Set<Dependable> getDependablesByRowIndex(final Dsm aDsm, final int aRow) {
		Set<Dependable> result = new HashSet<>();
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
	private static AnalysisResult getAnalysisResult(final Arguments aArguments,
			final Dependencies aDependencies) {
		return new ConfigurableDependencyAnalyzer(aArguments).analyze(aDependencies);
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void report() throws Exception {
		dsmHtmlWriter = new DsmHtmlWriter(outputDirectory, obfuscatePackageNames);

		List<String> sourcePathList = new ArrayList<>();
		sourcePathList.add(sourceDirectory);

		report(sourcePathList);
	}

	/**
	 * Parse dependencies from target.
	 * 
	 * @param aSourcePathList
	 *            path list of project source
	 * @throws Exception
	 */
	private void report(final List<String> aSourcePathList)
			throws Exception {
		Arguments arguments = new Arguments();
		arguments.setInput(aSourcePathList);

		DependencyEngine engine = new DependencyEngineFactory().getDependencyEngine(arguments);
		Dependencies dependencies = engine.getDependencies(arguments);
		DependencyGraph dependencyGraph = dependencies.getDependencyGraph();
		AnalysisResult analysisResult = getAnalysisResult(arguments, dependencies);
		Dsm dsm = new DsmEngine(dependencyGraph).createDsm();

		printDsmNavigation(dsm);
		printDsmForPackages(dsm, analysisResult);
		printDsmForClasses(dsm, dependencies, analysisResult, getPackageNames(dsm));

		copySiteSource();
	}

	/**
	 * Move source files from project source folder to the site folder.
	 * 
	 * @throws Exception
	 */
	private void copySiteSource() throws Exception {
		copyFileToSiteFolder(outputDirectory, "", "index.html");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.CSS_FOLDER_NAME, "style.css");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.IMAGE_FOLDER_NAME, "class.png");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.IMAGE_FOLDER_NAME, "package.png");
		copyFileToSiteFolder(outputDirectory, DsmHtmlWriter.IMAGE_FOLDER_NAME, "packages.png");
	}

	/**
	 * Print dsm for packages
	 * 
	 * @param aDsm
	 *            Project dependencies
	 * @param aAnalysisResult
	 *            Analysis result
	 */
	private void printDsmForPackages(final Dsm aDsm, final AnalysisResult aAnalysisResult)
			throws Exception {
		if (obfuscatePackageNames) {
			dsmHtmlWriter.printDsm(aDsm, aAnalysisResult, "all_packages",
					DsmHtmlWriter.FTL_PACKAGES_PAGE_TRUNC);
		} else {
			dsmHtmlWriter.printDsm(aDsm, aAnalysisResult, "all_packages",
					DsmHtmlWriter.FTL_PACKAGES_PAGE);
		}
	}

	/**
	 * Print DSM navigation
	 * 
	 * @param aDsm
	 * @throws Exception
	 */
	private void printDsmNavigation(Dsm aDsm) throws Exception {
		List<String> packageNames = getPackageNames(aDsm);
		dsmHtmlWriter.printDsmPackagesNavigation(packageNames);
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
			Set<Dependable> dependableSet = getDependablesByRowIndex(aDsm, packageIndex);

			DependencyGraph dependencyGraph = aDependencies.getDependencyGraph(scope, dependableSet,
					Dependencies.DependencyFilter.none);

			Dsm dsm = new DsmEngine(dependencyGraph).createDsm();

			dsmHtmlWriter.printDsm(dsm, aAnalysisResult, aPackageNames.get(packageIndex),
					DsmHtmlWriter.FTL_CLASSES_PAGE);
		}
	}

	/**
	 * Copy file to the site directory
	 * 
	 * @param aSiteDirectory
	 *            Report output directory
	 * @param aDirName
	 *            Directory name
	 * @param aFileName
	 *            File name
	 * @throws Exception
	 */
	private static void copyFileToSiteFolder(final String aSiteDirectory, final String aDirName,
			final String aFileName) {
		String fileName;

		// check directory
		if (aDirName != null && !aDirName.isEmpty()) {
			new File(aSiteDirectory + aDirName).mkdirs();
			fileName = aDirName + "/" + aFileName;
		} else {
			fileName = aFileName;
		}

		copyFileToSiteFolder(aSiteDirectory, fileName);
	}

	/**
	 * Copy file to the site directory
	 * 
	 * @param aSiteDirectory
	 *            Site directory
	 * @param aFileName
	 *            File name
	 * @throws Exception
	 */
	private static void copyFileToSiteFolder(final String aSiteDirectory, final String aFileName) {
		try (InputStream inputStream = DsmReportEngine.class.getResourceAsStream("/" + aFileName);
			 OutputStream outputStream = new FileOutputStream(new File(aSiteDirectory + aFileName))) {
			int numberOfBytes;
			byte[] buffer = new byte[1024];
			while ((numberOfBytes = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, numberOfBytes);
			}
		} catch (IOException e) {
			if (e instanceof FileNotFoundException) {
				throw new RuntimeException("Can't find " + aSiteDirectory + aFileName + " file. "
						+ e.getMessage(), e);
			} else {
				throw new RuntimeException("Unable to copy source file. " + e.getMessage(), e);
			}
		}
	}
}
