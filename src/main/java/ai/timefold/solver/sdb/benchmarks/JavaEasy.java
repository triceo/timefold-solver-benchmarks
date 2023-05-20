package ai.timefold.solver.sdb.benchmarks;

import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

import org.openjdk.jmh.annotations.Param;

public class JavaEasy extends AbstractBenchmark {

    @Param
    public Example easyExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.JAVA_EASY;
    }

    @Override
    protected Example getExample() {
        return easyExample;
    }
}
