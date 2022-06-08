package org.optaplanner.sdb.benchmarks;

import org.openjdk.jmh.annotations.Param;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public class ConstraintStreamsDrools extends AbstractBenchmark {

    @Param
    public Example csdExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.CONSTRAINT_STREAMS_DROOLS;
    }

    @Override
    protected Example getExample() {
        return csdExample;
    }
}
