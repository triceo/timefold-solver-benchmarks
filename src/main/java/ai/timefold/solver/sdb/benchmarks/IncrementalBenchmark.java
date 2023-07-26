package ai.timefold.solver.sdb.benchmarks;

import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

import org.openjdk.jmh.annotations.Param;

public class IncrementalBenchmark extends AbstractBenchmark {

    @Param
    public Example incrementalExample;

    @Override
    protected ScoreDirectorType getScoreDirectorType() {
        return ScoreDirectorType.INCREMENTAL;
    }

    @Override
    protected Example getExample() {
        return incrementalExample;
    }
}
