package ai.timefold.solver.sdb.benchmarks;

import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

import org.openjdk.jmh.annotations.Param;

public class ConstraintStreamsBenchmark extends AbstractBenchmark {

    @Param
    public Example csExample;

    @Override
    protected ScoreDirectorType getScoreDirectorType() {
        return ScoreDirectorType.CONSTRAINT_STREAMS;
    }

    @Override
    protected Example getExample() {
        return csExample;
    }
}
