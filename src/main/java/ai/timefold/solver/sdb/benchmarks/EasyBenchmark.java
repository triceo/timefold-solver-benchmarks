package ai.timefold.solver.sdb.benchmarks;

import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

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
