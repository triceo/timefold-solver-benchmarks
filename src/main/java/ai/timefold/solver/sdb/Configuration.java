package ai.timefold.solver.sdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

final class Configuration {

    private static final int DEFAULT_FORK_COUNT = 10;
    private static final int DEFAULT_WARMUP_ITERATIONS = 5;
    private static final int DEFAULT_MEASUREMENT_ITERATIONS = 5;
    private static final double DEFAULT_RELATIVE_SCORE_ERROR_THRESHOLD = 0.02;

    public static Configuration read(InputStream inputStream) throws IOException {
        Properties properties = new Properties();
        properties.load(inputStream);

        List<ScoreDirectorType> enabledScoreDirectorTypes;
        String scoreDirectorTypes = properties.getProperty("score_director_type");
        if (scoreDirectorTypes == null) {
            enabledScoreDirectorTypes = Arrays.asList(ScoreDirectorType.values());
        } else {
            enabledScoreDirectorTypes = Arrays.stream(scoreDirectorTypes.split("\\Q,\\E"))
                    .map(sdt -> switch (sdt) {
                        case "cs" -> ScoreDirectorType.CONSTRAINT_STREAMS;
                        case "cs_justified" -> ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED;
                        case "easy" -> ScoreDirectorType.EASY;
                        case "incremental" -> ScoreDirectorType.INCREMENTAL;
                        default -> throw new IllegalArgumentException("Unknown score director type: " + sdt);
                    })
                    .collect(Collectors.toList());
        }

        List<Example> enabledExamples;
        String examples = properties.getProperty("example");
        if (examples == null) {
            enabledExamples = Arrays.asList(Example.values());
        } else {
            enabledExamples = Arrays.stream(examples.split("\\Q,\\E"))
                    .map(e -> {
                        try {
                            return Example.valueOf(e.toUpperCase());
                        } catch (Exception ex) {
                            throw new IllegalArgumentException("Unknown example: " + e, ex);
                        }
                    })
                    .collect(Collectors.toList());
        }
        int forkCount = (int) parseDouble(properties, "forks", Integer.toString(DEFAULT_FORK_COUNT));
        int warmupIterations = (int) parseDouble(properties, "warmup_iterations", Integer.toString(DEFAULT_WARMUP_ITERATIONS));
        int measurementIterations =
                (int) parseDouble(properties, "measurement_iterations", Integer.toString(DEFAULT_MEASUREMENT_ITERATIONS));
        double relativeScoreErrorThreshold = parseDouble(properties, "relative_score_error_threshold",
                Double.toString(DEFAULT_RELATIVE_SCORE_ERROR_THRESHOLD));
        return new Configuration(enabledScoreDirectorTypes, enabledExamples, forkCount, warmupIterations, measurementIterations,
                relativeScoreErrorThreshold);
    }

    private static double parseDouble(Properties properties, String property, String def) {
        String propertyValue = properties.getProperty(property, def);
        try {
            return Double.parseDouble(propertyValue);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed parsing " + property + " " + propertyValue, ex);
        }
    }

    public static Configuration getDefault() {
        return new Configuration(Arrays.asList(ScoreDirectorType.values()), Arrays.asList(Example.values()), DEFAULT_FORK_COUNT,
                DEFAULT_WARMUP_ITERATIONS, DEFAULT_MEASUREMENT_ITERATIONS, DEFAULT_RELATIVE_SCORE_ERROR_THRESHOLD);
    }

    private final List<ScoreDirectorType> enabledScoreDirectorTypes;
    private final List<Example> enabledExamples;
    private final int forkCount;
    private final int warmupIterations;
    private final int measurementIterations;
    private final double relativeScoreErrorThreshold;

    private Configuration(List<ScoreDirectorType> enabledScoreDirectorTypes, List<Example> enabledExamples,
            int forkCount, int warmupIterations, int measurementIterations, double relativeScoreErrorThreshold) {
        this.enabledScoreDirectorTypes = enabledScoreDirectorTypes;
        this.enabledExamples = enabledExamples;
        this.forkCount = forkCount;
        this.warmupIterations = warmupIterations;
        this.measurementIterations = measurementIterations;
        this.relativeScoreErrorThreshold = relativeScoreErrorThreshold;
    }

    public List<ScoreDirectorType> getEnabledScoreDirectorTypes() {
        return enabledScoreDirectorTypes;
    }

    public List<Example> getEnabledExamples() {
        return enabledExamples;
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
