package org.optaplanner.sdb.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class DrlExample extends AbstractExample {

    @Param // All of them.
    public Example example;

    @Override
    protected ScoreDirector getScoreDirector() {
        return ScoreDirector.DRL;
    }

    @Override
    protected Example getExample() {
        return example;
    }
}
