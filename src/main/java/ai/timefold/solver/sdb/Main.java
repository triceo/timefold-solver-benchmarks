/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package ai.timefold.solver.sdb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final Path ASYNC_PROFILER_DIR = Path.of("async-profiler-2.9-linux-x64", "build")
            .toAbsolutePath();

    private static String leftPad(int input, int length) {
        return String.format("%1$" + length + "s", input)
                .replace(' ', '0');
    }

    private static String getTimestamp() {
        ZonedDateTime now = Instant.now().atZone(ZoneId.systemDefault());
        String year = leftPad(now.getYear(), 4);
        String month = leftPad(now.getMonthValue(), 2);
        String day = leftPad(now.getDayOfMonth(), 2);
        String hour = leftPad(now.getHour(), 2);
        String minute = leftPad(now.getMinute(), 2);
        String second = leftPad(now.getSecond(), 2);
        return year + "" + month + "" + day + "_" + hour + "" + minute + "" + second;
    }

    private static Configuration readConfiguration() {
        Path configPath = Path.of("benchmark.properties")
                .toAbsolutePath();
        if (configPath.toFile().exists()) {
            LOGGER.info("Using benchmark configuration file: {}.", configPath);
            try (InputStream inputStream = Files.newInputStream(configPath)) {
                return Configuration.read(inputStream);
            } catch (IOException e) {
                throw new IllegalStateException("Failed reading benchmark properties: " + configPath, e);
            }
        } else {
            LOGGER.info("Using default benchmark configuration.");
            return Configuration.getDefault();
        }
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Configuration configuration = readConfiguration();

        File resultFolder = new File("results/" + getTimestamp());
        File benchmarkResults = new File(resultFolder, "benchmarkResults.csv");
        resultFolder.mkdirs();

        ChainedOptionsBuilder options = new OptionsBuilder()
                .forks(10)
                .warmupIterations(5)
                .measurementIterations(5)
                .jvmArgs("-XX:+UseParallelGC", "-Xms1g", "-Xmx1g") // Minimize GC overhead.
                .result(benchmarkResults.getAbsolutePath())
                .resultFormat(ResultFormatType.CSV)
                .shouldDoGC(true);

        options = processBenchmark(options, configuration, ScoreDirectorType.CONSTRAINT_STREAMS);
        options = processBenchmark(options, configuration, ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED);
        options = processBenchmark(options, configuration, ScoreDirectorType.EASY);
        options = processBenchmark(options, configuration, ScoreDirectorType.INCREMENTAL);

        Path asyncProfilerPath = ASYNC_PROFILER_DIR.resolve("libasyncProfiler.so")
                .toAbsolutePath();
        if (asyncProfilerPath.toFile().exists()) {
            LOGGER.info("Using Async profiler from {}.", asyncProfilerPath);
            options = options.addProfiler(AsyncProfiler.class,
                    "event=cpu;alloc;" +
                            "output=jfr;" +
                            "dir=" + resultFolder.getAbsolutePath() + ";" +
                            "libPath=" + asyncProfilerPath);
        } else {
            LOGGER.warn("Async profiler not found in {}. Profiler disabled.", asyncProfilerPath);
        }

        new Runner(options.build()).run();

        if (asyncProfilerPath.toFile().exists()) {
            Files.walk(resultFolder.toPath())
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".jfr"))
                    .forEach(path -> {
                        LOGGER.info("Found JFR file: {}.", path);
                        generateFlameGraphsFromJfr(path, "cpu");
                        generateFlameGraphsFromJfr(path, "alloc");
                    });
        } else {
            LOGGER.warn("Skipping JFR conversion in '{}'.", resultFolder);
        }
    }

    private static void generateFlameGraphsFromJfr(Path jfrFilePath, String type) {
        Path targetPath = Path.of(jfrFilePath.toAbsolutePath().getParent().toString(), type + ".html");
        String[] args = new String[] {
                "--simple",
                "--" + type,
                jfrFilePath.toString(),
                targetPath.toString()
        };
        try { // Converter is stupidly in an unnamed package.
            Class fooClass = Class.forName("jfr2flame");
            Method fooMethod = fooClass.getMethod("main", String[].class);
            fooMethod.invoke(null, (Object) args);
            LOGGER.info("Generating flame graph succeeded: {}.", args);
        } catch (Exception ex) {
            LOGGER.error("Generating flame graph failed: {}.", args, ex);
        }
    }

    private static ChainedOptionsBuilder processBenchmark(ChainedOptionsBuilder options, Configuration configuration,
            ScoreDirectorType scoreDirectorType) {
        String[] supportedExampleNames = getSupportedExampleNames(configuration, scoreDirectorType);
        if (supportedExampleNames.length > 0) {
            options = options.include(scoreDirectorType.getBenchmarkClass().getSimpleName())
                    .param(scoreDirectorType.getBenchmarkParamName(), supportedExampleNames);
        }
        return options;
    }

    private static String[] getSupportedExampleNames(Configuration configuration, ScoreDirectorType scoreDirectorType) {
        if (!configuration.getEnabledScoreDirectorTypes().contains(scoreDirectorType)) {
            LOGGER.warn("No examples enabled for score director type " + scoreDirectorType);
            return new String[0];
        }
        String[] examples = configuration.getEnabledExamples()
                .stream()
                .filter(example -> example.isSupportedOn(scoreDirectorType))
                .map(Enum::name)
                .toArray(String[]::new);
        LOGGER.info("Examples enabled for score director type {}: {}", scoreDirectorType, examples);
        return examples;
    }

}
