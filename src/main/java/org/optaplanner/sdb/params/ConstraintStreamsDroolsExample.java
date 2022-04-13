package org.optaplanner.sdb.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ConstraintStreamsDroolsExample extends AbstractExample {

    @Param
    public Example csdExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.CONSTRAINT_STREAMS_DROOLS;
    }

    @Override
    protected Example getExample() {
        return csdExample;
    }
}
