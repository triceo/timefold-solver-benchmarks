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

package org.optaplanner.sdb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.optaplanner.sdb.params.ConstraintStreamsBavetExample;
import org.optaplanner.sdb.params.ConstraintStreamsDroolsExample;
import org.optaplanner.sdb.params.DrlExample;
import org.optaplanner.sdb.params.JavaEasyExample;
import org.optaplanner.sdb.params.JavaIncrementalExample;
import org.optaplanner.sdb.params.ScoreDirectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 10) // 5 has been demonstrated to be too little.
@Measurement(iterations = 5)
@Fork(5)
public class ScoreDirectorBenchmark {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreDirectorBenchmark.class);

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

    @Benchmark
    public Object droolsDrl(DrlExample params) {
        return params.problem.runInvocation();
    }

    @Benchmark
    public Object droolsCsd(ConstraintStreamsDroolsExample params) {
        return params.problem.runInvocation();
    }

    @Benchmark
    public Object javaCsb(ConstraintStreamsBavetExample params) {
        return params.problem.runInvocation();
    }

    @Benchmark
    public Object javaIncremental(JavaIncrementalExample params) {
        return params.problem.runInvocation();
    }

    @Benchmark
    public Object javaEasy(JavaEasyExample params) {
        return params.problem.runInvocation();
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

    public static void main(String[] args) throws RunnerException {
        Configuration configuration = readConfiguration();

        File resultFolder = new File("results/" + getTimestamp());
        File benchmarkResults = new File(resultFolder, "benchmarkResults.csv");
        resultFolder.mkdirs();

        ChainedOptionsBuilder options = new OptionsBuilder()
                .include(ScoreDirectorBenchmark.class.getSimpleName())
                .jvmArgs("-XX:+UseSerialGC", "-Xms1g", "-Xmx1g") // Minimize GC overhead.
                .param("drlExample", getSupportedExampleNames(configuration, ScoreDirectorType.DRL))
                .param("csdExample", getSupportedExampleNames(configuration, ScoreDirectorType.CONSTRAINT_STREAMS_DROOLS))
                .param("csbExample", getSupportedExampleNames(configuration, ScoreDirectorType.CONSTRAINT_STREAMS_BAVET))
                .param("easyExample", getSupportedExampleNames(configuration, ScoreDirectorType.JAVA_EASY))
                .param("incrementalExample", getSupportedExampleNames(configuration, ScoreDirectorType.JAVA_INCREMENTAL))
                .result(benchmarkResults.getAbsolutePath())
                .resultFormat(ResultFormatType.CSV);

        Path asyncProfilerPath = Path.of("async-profiler-2.7-linux-x64", "build", "libasyncProfiler.so")
                .toAbsolutePath();
        if (asyncProfilerPath.toFile().exists()) {
            LOGGER.info("Using Async profiler from {}.", asyncProfilerPath);
            options = options.addProfiler(AsyncProfiler.class,
                    "event=cpu;" +
                            "output=flamegraph,tree;" +
                            "dir=" + resultFolder.getAbsolutePath() + ";" +
                            "libPath=" + asyncProfilerPath + ";" +
                            "simple=true");
        } else {
            LOGGER.info("Async profiler not found in {}. Profiler disabled.", asyncProfilerPath);
        }

        new Runner(options.build()).run();
    }

    private static String[] getSupportedExampleNames(Configuration configuration, ScoreDirectorType scoreDirectorType) {
        if (!configuration.getEnabledScoreDirectorTypes().contains(scoreDirectorType)) {
            LOGGER.info("No examples enabled for score director type " + scoreDirectorType);
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
