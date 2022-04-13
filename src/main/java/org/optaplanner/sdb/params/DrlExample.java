package org.optaplanner.sdb.params;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class DrlExample extends AbstractExample {

    @Param
    public Example drlExample;

    @Override
    protected ScoreDirectorType getScoreDirector() {
        return ScoreDirectorType.DRL;
    }

    @Override
    protected Example getExample() {
        return drlExample;
    }
}
