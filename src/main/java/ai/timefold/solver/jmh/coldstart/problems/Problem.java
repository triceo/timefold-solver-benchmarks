package ai.timefold.solver.jmh.coldstart.problems;

import org.openjdk.jmh.infra.Blackhole;

public interface Problem {

    void setupTrial();

    void setupIteration();

    void setupInvocation();

    Object runInvocation(Blackhole blackhole);

    void tearDownInvocation();

    void tearDownIteration();

    void teardownTrial();

}
