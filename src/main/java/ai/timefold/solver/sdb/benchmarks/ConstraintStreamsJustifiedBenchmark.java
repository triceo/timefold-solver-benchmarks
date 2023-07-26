package ai.timefold.solver.sdb.benchmarks;

import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

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
