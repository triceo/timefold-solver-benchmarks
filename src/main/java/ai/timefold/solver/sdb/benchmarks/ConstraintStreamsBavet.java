package ai.timefold.solver.sdb.benchmarks;

import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

import org.openjdk.jmh.annotations.Param;

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
