package ai.timefold.solver.jmh.scoredirector.benchmarks;

import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;

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
