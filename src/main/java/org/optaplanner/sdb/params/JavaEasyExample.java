package org.optaplanner.sdb.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JavaEasyExample extends AbstractExample {

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
