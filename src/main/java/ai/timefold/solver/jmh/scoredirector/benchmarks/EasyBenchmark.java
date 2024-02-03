package ai.timefold.solver.jmh.scoredirector.benchmarks;

import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;
import ai.timefold.solver.jmh.scoredirector.Example;

import org.openjdk.jmh.annotations.Param;

public class EasyBenchmark extends AbstractBenchmark {

    @Param
    public Example easyExample;

    @Override
    protected ScoreDirectorType getScoreDirectorType() {
        return ScoreDirectorType.EASY;
    }

    @Override
    protected Example getExample() {
        return easyExample;
    }
}
