package ai.timefold.solver.sdb.benchmarks;

import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

import org.openjdk.jmh.annotations.Param;

public class ConstraintStreamsBavetJustified extends AbstractBenchmark {

    @Param
    public Example csbExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.CONSTRAINT_STREAMS_BAVET_JUSTIFIED;
    }

    @Override
    protected Example getExample() {
        return csbExample;
    }
}
