package org.testpackage.test.optimization;

import org.junit.Before;
import org.junit.Test;
import org.testpackage.optimization.ClassCoverage;
import org.testpackage.optimization.TestCoverageRepository;

import java.io.File;
import java.io.IOException;

import static org.testpackage.VisibleAssertions.assertEquals;
import static org.testpackage.optimization.ClassCoverage.coverageAsString;
import static org.testpackage.optimization.ClassCoverage.coverageFromString;

/**
 * @author richardnorth
 */
public class TestCoverageRepositoryTest {

    private String tempFileName;

    @Before
    public void setup() throws IOException {
        final File tempFile = File.createTempFile("coverage-repo-test", "dat");
        tempFile.delete();
        tempFileName = tempFile.getAbsolutePath();
    }

    @Test
    public void testSimpleOrdering() throws IOException, ClassNotFoundException {

        TestCoverageRepository repository = new TestCoverageRepository(tempFileName);

        repository.addCoverage("testA",
                1L,
                new ClassCoverage("class1", coverageFromString(" XX"), 3, 1L),
                new ClassCoverage("class2", coverageFromString(" X "), 3, 1L)
                );
        repository.addCoverage("testB",
                1L,
                new ClassCoverage("class1", coverageFromString(" XX"), 3, 1L),
                new ClassCoverage("class3", coverageFromString("XXX"), 3, 1L)
                );
        repository.addCoverage("testC",
                1L,
                new ClassCoverage("class4", coverageFromString("XXX"), 3, 1L)
                );

        final ClassCoverage testCoverageA = repository.getCoverage("testA");
        assertEquals("expected coverage", " XX X       ", coverageAsString(testCoverageA));

        final ClassCoverage testCoverageB = repository.getCoverage("testB");
        assertEquals("expected coverage", " XX   XXX   ", coverageAsString(testCoverageB));

        final ClassCoverage testCoverageC = repository.getCoverage("testC");
        assertEquals("expected coverage", "         XXX", coverageAsString(testCoverageC));

        repository.save();
    }

    @Test
    public void testSaveAndLoad() throws IOException, ClassNotFoundException {
        TestCoverageRepository repository = new TestCoverageRepository(tempFileName);
        repository.addCoverage("testA",
                        1L,
                        new ClassCoverage("class1", coverageFromString(" XX"), 3, 1L),
                        new ClassCoverage("class2", coverageFromString(" X "), 3, 1L)
                        );
        repository.addCoverage("testB",
                1L,
                new ClassCoverage("class1", coverageFromString(" XX"), 3, 1L),
                new ClassCoverage("class3", coverageFromString("XXX"), 3, 1L)
                );
        repository.save();

        final TestCoverageRepository newRepository = new TestCoverageRepository(tempFileName);
        assertEquals("coverage for each test is the same after saving and loading", repository.getCoverage("testA"), newRepository.getCoverage("testA"));
        assertEquals("coverage for each test is the same after saving and loading", repository.getCoverage("testB"), newRepository.getCoverage("testB"));
    }
}
