package ai.timefold.solver.jmh.scoredirector.benchmarks;

import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;

import org.openjdk.jmh.annotations.Param;

public class ConstraintStreamsJustifiedBenchmark extends AbstractBenchmark {

    @Param
    public Example csJustifiedExample;

    @Override
    protected ScoreDirectorType getScoreDirectorType() {
        return ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED;
    }

    @Override
    protected Example getExample() {
        return csJustifiedExample;
    }
}
