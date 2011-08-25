package uirstestpackage;

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

	/**
	 * Link to dtangler DSM class
	 */
	Dsm dsm;
	/**
	 * Create the exemplar of DsmHtmlWriter class
	 */
	DsmHtmlWriter textUI = new DsmHtmlWriter();

	String targetPath = "/target/site/DSM/";

	// List<Integer> selectedCols = Collections.EMPTY_LIST;
	// List<Integer> selectedRows = new ArrayList<Integer>();

	/**
	 * Program arguments. Example:
	 * -input=/home/yuriy/programming/Java/checkstyle-5.3-all.jar;
	 * 
	 * @param args
	 */
	public Main(String[] args) {
		String[] arg = new String[1];
		arg[0] = "-input=" + System.getProperty("user.dir") + "/target/classes";
		System.out.println("[DSM] getDescription " + arg[0]);
		run(arg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main(args);
	}

	/**
	 * Parse dependencies from target Program arguments. Example:
	 * -input=/home/yuriy/programming/Java/checkstyle-5.3-all.jar;
	 * 
	 * @param args
	 */
	public boolean run(String[] args) {
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
			// selectedRows.add(new Integer(packageIndex));

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
		moveSource();
		return true;
	}

	private void moveSource() {
		CreateTheDirectories();
		CopyFilesToSite("index.html");
		CopyFilesToSite("css/style.css");
		CopyFilesToSite("images/class.png");
		CopyFilesToSite("images/package.png");
		CopyFilesToSite("images/packages.png");
	}

	private void CreateTheDirectories() {
		String[] dirs = { "images", "css" };
		for (String dir : dirs) {
			File outputFile = new File(System.getProperty("user.dir")
					+ targetPath + dir);
			if (!outputFile.exists()) {
				outputFile.mkdir();
				System.out.println("[CopyFilesToSite] new Folder '" + dir
						+ "' created");
			}

		}
	}

	private boolean CopyFilesToSite(String directoryOrFileName) {
		try {
			System.out.println(String.format(
					"[CopyFilesToSite] Copy %s started", directoryOrFileName));
			File outputFile = new File(System.getProperty("user.dir")
					+ targetPath + directoryOrFileName);

			InputStream inputStream = getClass().getResourceAsStream(
					"/" + directoryOrFileName);
			OutputStream outputStream = new FileOutputStream(outputFile);
			byte[] buf = new byte[1024];
			int len;

			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			inputStream.close();
			outputStream.close();

			System.out.println("[CopyFilesToSite] " + directoryOrFileName
					+ " successfully copied ");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @param aDsm
	 * @return
	 */
	private List<String> getPackageNames(Dsm aDsm) {
		List<DsmRow> dsmRowList = aDsm.getRows();
		List<String> packageNames = new ArrayList<String>();

		for (int index = 0; index < dsmRowList.size(); index++) {
			DsmRow dsmRow = dsmRowList.get(index);
			packageNames.add(dsmRow.getDependee().toString());
		}
		return packageNames;
	}

	/**
	 * @param aDependencies
	 * @param aArguments
	 * @param aDependencyGraph
	 * @param aPackageName
	 */
	public void analisisAndPrintDsm(Dependencies aDependencies,
			Arguments aArguments, DependencyGraph aDependencyGraph,
			String aPackageName) {
		AnalysisResult analysisResult = getAnalysisResult(aArguments,
				aDependencies);
		printDsm(aDependencyGraph, analysisResult, aPackageName);
	}

	/**
	 * @param aDependencies
	 * @param aArguments
	 * @param aDependencyGraph
	 * @param aPackageName
	 */
	public void analisisAndPrintDsmPackages(Dependencies aDependencies,
			Arguments aArguments, DependencyGraph aDependencyGraph,
			String aPackageName) {
		AnalysisResult analysisResult = getAnalysisResult(aArguments,
				aDependencies);
		textUI.printDsmPackages(new DsmEngine(aDependencyGraph).createDsm(),
				analysisResult, aPackageName);
	}

	/**
	 * @param aRow
	 * @return
	 */
	public Set<Dependency> getSelectionDependencies(final int aRow) {
		Set<Dependency> result = new HashSet<Dependency>();
		// for (Integer col : selectedCols)
		// for (Integer row : selectedRows)
		result.add(getDsmCell(0, 0).getDependency());
		return result;
	}

	/**
	 * @param aRow
	 * @return
	 */
	public Set<Dependable> getSelectionDependables(final int aRow) {
		// if (selectedRows.isEmpty())
		// return Collections.EMPTY_SET;
		// Set<Dependency> selectionDependencies =
		// getSelectionDependencies(aRow);
		Set<Dependency> selectionDependencies = new HashSet<Dependency>();
		Set<Dependable> result = new HashSet<Dependable>();
		if (selectionDependencies.isEmpty()) {
			// for (int row : selectedRows)
			result.add(dsm.getRows().get(aRow).getDependee());
		} else {
			for (Dependency dependency : selectionDependencies) {
				result.add(dependency.getDependant());
				result.add(dependency.getDependee());
			}
		}
		return result;
	}

	private DsmCell getDsmCell(final int row, final int col) {
		return dsm.getRows().get(row).getCells().get(col);
	}

	private AnalysisResult getAnalysisResult(Arguments arguments,
			Dependencies dependencies) {
		return new ConfigurableDependencyAnalyzer(arguments)
				.analyze(dependencies);
	}

	private void printDsm(DependencyGraph aDependencies,
			AnalysisResult aAnalysisResult, String aPackageName) {
		textUI.printDsm(new DsmEngine(aDependencies).createDsm(),
				aAnalysisResult, aPackageName);
	}

	private void printPackagesNavigationMenu(List<String> aPackageNames) {
		textUI.printNavigateDsmPackages(aPackageNames);
	}
}
