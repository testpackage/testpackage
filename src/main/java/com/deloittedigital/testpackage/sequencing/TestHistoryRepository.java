package com.deloittedigital.testpackage.sequencing;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Stores historic 'runs since last failure' counts on the filesystem for
 * persistence between test runs.
 *
 * Created by richardnorth on 01/01/2014.
 */
public class TestHistoryRepository {

    private static final int FAILURE_JUST_NOW = -1;
    private final File backingFile;
    private final Map<String, Integer> runsSinceLastFailures;

    /**
     * @param path relative or absolute path of the backing file where the historic data should be stored
     * @throws IOException
     */
    public TestHistoryRepository(String path) throws IOException {
        backingFile = new File(path);

        if (backingFile.exists()) {
            runsSinceLastFailures = Files.readLines(backingFile, Charsets.UTF_8, new LineProcessor<Map<String, Integer>>() {

                private Map<String, Integer> map = Maps.newHashMap();

                @Override
                public boolean processLine(String line) throws IOException {
                    String[] splitLine = line.split("\\s{4}");
                    String description = splitLine[0];
                    String runsSinceLastFailure = splitLine[1];

                    map.put(description, Integer.valueOf(runsSinceLastFailure));
                    return true;
                }

                @Override
                public Map<String, Integer> getResult() {
                    return map;
                }
            });
        } else {
            runsSinceLastFailures = Maps.newHashMap();
        }
    }

    /**
     *
     * @return an immutable copy of a map from test class/method description to the count of how many runs since last failure.
     */
    public Map<String, Integer> getRunsSinceLastFailures() {
        return ImmutableMap.copyOf(runsSinceLastFailures);
    }

    /**
     * Save the current state of historic data to the repository's backing file.
     * @throws IOException
     */
    public void save() throws IOException {

        backingFile.delete();

        BufferedWriter writer = null;
        try {
            writer = Files.newWriter(backingFile, Charsets.UTF_8);

            for (Map.Entry<String, Integer> entry : runsSinceLastFailures.entrySet()) {
                // Increment the run count since all failures
                int runsSinceFailure = entry.getValue() + 1;

                writer.write(String.format("%s    %d\n", entry.getKey(), runsSinceFailure));
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Mark a test class and method description as having just failed.
     * @param classDescription      a class description (i.e. the fully qualified class name)
     * @param methodDescription     a method description (i.e. as returned by Description#getDisplayName())
     */
    public void markFailure(String classDescription, String methodDescription) {
        runsSinceLastFailures.put(classDescription, FAILURE_JUST_NOW);
        runsSinceLastFailures.put(methodDescription, FAILURE_JUST_NOW);
    }
}
