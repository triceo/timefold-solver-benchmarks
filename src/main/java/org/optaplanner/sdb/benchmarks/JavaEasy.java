package org.optaplanner.sdb.benchmarks;

import org.openjdk.jmh.annotations.Param;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

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
