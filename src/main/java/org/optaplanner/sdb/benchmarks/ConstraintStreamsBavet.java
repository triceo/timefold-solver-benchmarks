package org.optaplanner.sdb.benchmarks;

import org.openjdk.jmh.annotations.Param;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public class ConstraintStreamsBavet extends AbstractBenchmark {

    @Param
    public Example csbExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.CONSTRAINT_STREAMS_BAVET;
    }

    @Override
    protected Example getExample() {
        return csbExample;
    }
}
