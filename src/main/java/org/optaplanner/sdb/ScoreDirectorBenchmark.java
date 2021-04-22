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

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.AsyncProfiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.optaplanner.sdb.params.*;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@State(Scope.Benchmark)
@Warmup(iterations = 10) // 5 has been demonstrated to be too little.
@BenchmarkMode(Mode.Throughput)
public class ScoreDirectorBenchmark {

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
    public Object drl(DrlExample params) {
        return params.problem.runInvocation();
    }

    @Benchmark
    public Object csd(ConstraintStreamsDroolsExample params) {
        return params.problem.runInvocation();
    }

    @Benchmark
    public Object csb(ConstraintStreamsBavetExample params) {
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

    public static void main(String[] args) throws RunnerException {
        File resultFolder = new File("results/" + getTimestamp());
        File benchmarkResults = new File(resultFolder, "benchmarkResults.csv");
        resultFolder.mkdirs();

        String asyncProfilerAbsolutePath = new File("async-profiler-1.8.5-linux-x64/build/libasyncProfiler.so")
                .getAbsolutePath();
        Options options = new OptionsBuilder()
                .include(ScoreDirectorBenchmark.class.getSimpleName())
                .addProfiler(AsyncProfiler.class,
                        "event=cpu;" +
                                "output=flamegraph,tree;" +
                                "dir=" + resultFolder.getAbsolutePath() + ";" +
                                "libPath=" + asyncProfilerAbsolutePath + ";" +
                                "simple=true")
                .jvmArgs("-Xms2g", "-Xmx2g")
                .result(benchmarkResults.getAbsolutePath())
                .resultFormat(ResultFormatType.CSV)
                .build();
        new Runner(options).run();
    }

}
