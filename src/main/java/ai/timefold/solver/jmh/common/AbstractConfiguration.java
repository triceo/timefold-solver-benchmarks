package ai.timefold.solver.jmh.common;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public abstract class AbstractConfiguration {

    protected static final int DEFAULT_FORK_COUNT = 10;
    protected static final int DEFAULT_WARMUP_ITERATIONS = 5;
    protected static final int DEFAULT_MEASUREMENT_ITERATIONS = 5;
    protected static final double DEFAULT_RELATIVE_SCORE_ERROR_THRESHOLD = 0.02;

    public static BenchmarkProperties readBenchmarkProperties(Properties properties, AbstractConfiguration defaultConfiguration) {
        var forkCount = (int) AbstractConfiguration.parseDouble(properties, "forks",
                Integer.toString(defaultConfiguration.getForkCount()));
        var warmupIterations = (int) AbstractConfiguration.parseDouble(properties, "warmup_iterations",
                Integer.toString(defaultConfiguration.getWarmupIterations()));
        var measurementIterations =
                (int) AbstractConfiguration.parseDouble(properties, "measurement_iterations",
                        Integer.toString(defaultConfiguration.getMeasurementIterations()));
        var relativeScoreErrorThreshold = AbstractConfiguration.parseDouble(properties, "relative_score_error_threshold",
                Double.toString(defaultConfiguration.getRelativeScoreErrorThreshold()));
        return new BenchmarkProperties(forkCount, warmupIterations, measurementIterations, relativeScoreErrorThreshold);
    }

    protected static double parseDouble(Properties properties, String property, String def) {
        var propertyValue = properties.getProperty(property, def);
        try {
            return Double.parseDouble(propertyValue);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed parsing " + property + " " + propertyValue, ex);
        }
    }

    protected static <E> List<E> parseExamples(String examples, E... values) {
        if (examples == null) {
            return Arrays.asList(values);
        } else {
            return Arrays.stream(examples.split("\\Q,\\E"))
                    .map(e -> Arrays.stream(values)
                            .filter(value -> value.toString().equalsIgnoreCase(e))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Unknown example: " + e)))
                    .collect(Collectors.toList());
        }
    }

    private final int forkCount;
    private final int warmupIterations;
    private final int measurementIterations;
    private final double relativeScoreErrorThreshold;

    protected AbstractConfiguration(int forkCount, int warmupIterations, int measurementIterations,
            double relativeScoreErrorThreshold) {
        this.forkCount = forkCount;
        this.warmupIterations = warmupIterations;
        this.measurementIterations = measurementIterations;
        this.relativeScoreErrorThreshold = relativeScoreErrorThreshold;
    }

    public int getForkCount() {
        return forkCount;
    }

    public int getWarmupIterations() {
        return warmupIterations;
    }

    public int getMeasurementIterations() {
        return measurementIterations;
    }

    public double getRelativeScoreErrorThreshold() {
        return relativeScoreErrorThreshold;
    }
}
