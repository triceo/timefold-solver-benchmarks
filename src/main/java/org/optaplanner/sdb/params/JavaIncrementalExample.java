package org.optaplanner.sdb.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class JavaIncrementalExample extends AbstractExample {

    @Param({"CLOUD_BALANCING", "INVESTMENT", "MACHINE_REASSIGNMENT", "NQUEENS", "PROJECT_JOB_SCHEDULING", "TSP", "VEHICLE_ROUTING"})
    public Example example;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.JAVA_INCREMENTAL;
    }

    @Override
    protected Example getExample() {
        return example;
    }
}
