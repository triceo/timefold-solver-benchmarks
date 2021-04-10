package org.optaplanner.examples.app.params;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.app.problems.Problem;

import java.util.Random;

public abstract class AbstractExample {

    private final Random random = new Random(0L);

    public Problem problem;

    abstract protected ScoreDirector getScoreDirector();

    abstract protected Example getExample();

    @Setup(Level.Trial)
    public void setupTrial() {
        problem = getExample().create(getScoreDirector());
        problem.setupTrial();
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        problem.setupIteration();
    }

    @Setup(Level.Invocation)
    public void setupInvocation() {
        problem.setupInvocation();
    }

    @TearDown(Level.Invocation)
    public void teardownInvocation() {
        problem.tearDownInvocation();
    }

    @TearDown(Level.Iteration)
    public void teardownIteration() {
        problem.tearDownIteration();
    }

    @TearDown(Level.Trial)
    public void teardownTrial() {
        problem.teardownTrial();
    }

}
