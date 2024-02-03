package ai.timefold.solver.jmh.scoredirector.benchmarks;

import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;

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
