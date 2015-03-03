package fr.inria.lille.repair;

import fr.inria.lille.commons.synthesis.smt.solver.SolverFactory;
import fr.inria.lille.repair.nopol.NoPol;
import fr.inria.lille.repair.nopol.NoPolLauncher;
import fr.inria.lille.repair.symbolic.SymbolicLauncher;
import fr.inria.lille.repair.common.patch.Patch;
import fr.inria.lille.repair.common.synth.StatementType;
import xxl.java.container.classic.MetaSet;
import xxl.java.junit.TestCase;
import xxl.java.junit.TestCasesListener;
import xxl.java.junit.TestSuiteExecution;
import xxl.java.library.FileLibrary;
import xxl.java.library.JavaLibrary;

import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Thomas Durieux on 03/03/15.
 */
public abstract class TestUtility {
    private String solver = "z3";
    private String solverPath = "/home/spirals/git/z3/build/z3"; // "lib/z3-4.3.2/z3_for_mac"
    private String realBugPath = "../../nopol-dataset/";
    private String executionType;

    public TestUtility(String executionType) {
        this.executionType = executionType;
    }

    public ProjectReference projectForExample(int nopolExampleNumber) {
        String sourceFile = "../test-projects/src/";
        String classpath = "../test-projects/target/test-classes:../test-projects/target/classes";
        String[] testClasses = new String[] { executionType + "_examples." + executionType + "_example_"
                + nopolExampleNumber + ".NopolExampleTest" };
        return new ProjectReference(sourceFile, classpath, testClasses);
    }

    private List<Patch> patchFor(ProjectReference project, StatementType type) {
        clean(project.sourceFile().getParent());
        List<Patch> patches;
        switch (this.executionType) {
            case "symbolic":
                patches = SymbolicLauncher.run(project, type);
                break;
            case "nopol":
                NoPol nopol = new NoPol(project.sourceFile(), project.classpath(), type);
                patches = nopol.build(project.testClasses());
                break;
            default:
                throw new RuntimeException("Execution type not found");
        }

        clean(project.sourceFile().getParent());
        return patches;
    }

    protected void fixComparison(Patch foundPatch, String... expectedFixes) {
        Collection<String> possibleFixes = MetaSet.newHashSet(expectedFixes);
        assertTrue(foundPatch + "is not a valid patch",
                possibleFixes.contains(foundPatch.asString()));
    }

    protected Patch test(int projectNumber, int linePosition, StatementType type,
                       Collection<String> expectedFailedTests) {
        ProjectReference project = projectForExample(projectNumber);
        SolverFactory.setSolver(solver, solverPath);
        TestCasesListener listener = new TestCasesListener();
        URLClassLoader classLoader = new URLClassLoader(project.classpath());
        TestSuiteExecution.runCasesIn(project.testClasses(), classLoader,
                listener);
        Collection<String> failedTests = TestCase.testNames(listener
                .failedTests());
        // assertEquals(expectedFailedTests.size(), failedTests.size());
        // assertTrue(expectedFailedTests.containsAll(failedTests));
        List<Patch> patches = patchFor(project, type);
        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), type);
        assertEquals(linePosition, patch.getLineNumber());
        System.out.println(String.format("Patch for nopol example %d: %s",
                projectNumber, patch.asString()));
        return patch;
    }

    protected Patch testRealBug(String projectName, boolean isMaven, StatementType statementType, String[] tests, String... dependencies) {
        String rootFolder = realBugPath + projectName + "/";
        String srcFolder = rootFolder + "src/";
        String binFolder = rootFolder + "bin/";
        if(isMaven) {
            binFolder = rootFolder + "target/classes:" + rootFolder + "target/test-classes";
        }
        String libFolder = rootFolder + "lib/";

        String classpath = binFolder + ":" + "misc/nopol-example/junit-4.11.jar:";
        for (int i = 0; i<dependencies.length; i++) {
            classpath += libFolder + dependencies[i];
            if(i<dependencies.length -1) {
                classpath += ":";
            }
        }
        SolverFactory.setSolver(solver, solverPath);
        List<Patch> patches;
        switch (this.executionType) {
            case "symbolic":
                patches = SymbolicLauncher
                        .launch(FileLibrary.openFrom(srcFolder),
                                JavaLibrary.classpathFrom(classpath),
                                statementType,
                                tests);
                break;
            case "nopol":
                patches = NoPolLauncher
                        .launch(FileLibrary.openFrom(srcFolder),
                                JavaLibrary.classpathFrom(classpath),
                                statementType,
                                tests);
                break;
            default:
                throw new RuntimeException("Execution type not found");
        }
        assertEquals(patches.toString(), 1, patches.size());
        Patch patch = patches.get(0);
        assertEquals(patch.getType(), statementType);
        System.out.println(String.format("Patch for real bug %s: %s",
                projectName, patch.asString()));
        clean(srcFolder);
        return patch;
    }

    private void clean(String folderPath) {
        String path = folderPath + "/spooned";
        if (FileLibrary.isValidPath(path)) {
            FileLibrary.deleteDirectory(path);
        }
    }
}