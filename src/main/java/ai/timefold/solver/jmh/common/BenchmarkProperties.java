package ai.timefold.solver.jmh.common;

import java.util.Properties;

public record BenchmarkProperties(int forkCount, int warmupIterations, int measurementIterations, double relativeScoreErrorThreshold) {

}
