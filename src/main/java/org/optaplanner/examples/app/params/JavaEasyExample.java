package org.optaplanner.examples.app.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.optaplanner.examples.app.directors.ScoreDirector;

@State(Scope.Benchmark)
public class JavaEasyExample extends AbstractExample {

    @Param({"CLOUD_BALANCING", "COACH_SHUTTLE_GATHERING", "INVESTMENT", "NQUEENS", "TSP", "VEHICLE_ROUTING"})
    public Example example;

    @Override
    protected ScoreDirector getScoreDirector() {
        return ScoreDirector.JAVA_EASY;
    }

    @Override
    protected Example getExample() {
        return example;
    }
}
