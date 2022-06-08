package org.optaplanner.sdb.benchmarks;

import org.openjdk.jmh.annotations.Param;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public class Drl extends AbstractBenchmark {

    @Param
    public Example drlExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.DRL;
    }

    @Override
    protected Example getExample() {
        return drlExample;
    }
}
