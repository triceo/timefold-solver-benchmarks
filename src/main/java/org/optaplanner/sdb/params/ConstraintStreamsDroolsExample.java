package org.optaplanner.sdb.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ConstraintStreamsDroolsExample extends AbstractExample {

    @Param // All of them.
    public Example example;

    @Override
    protected ScoreDirector getScoreDirector() {
        return ScoreDirector.CONSTRAINT_STREAMS_DROOLS;
    }

    @Override
    protected Example getExample() {
        return example;
    }
}
