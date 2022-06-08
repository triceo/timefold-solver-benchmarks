package org.optaplanner.sdb.benchmarks;

import org.openjdk.jmh.annotations.Param;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public class JavaIncremental extends AbstractBenchmark {

    @Param
    public Example incrementalExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.JAVA_INCREMENTAL;
    }

    @Override
    protected Example getExample() {
        return incrementalExample;
    }
}
