package org.optaplanner.sdb.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ConstraintStreamsBavetExample extends AbstractExample {

    @Param({"NQUEENS", "ROCK_TOUR", "TASK_ASSIGNING", "VEHICLE_ROUTING"})
    public Example example;

    @Override
    protected ScoreDirector getScoreDirector() {
        return ScoreDirector.CONSTRAINT_STREAMS_BAVET;
    }

    @Override
    protected Example getExample() {
        return example;
    }
}
