package ai.timefold.solver.jmh.scoredirector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import ai.timefold.solver.jmh.common.AbstractConfiguration;

final class Configuration extends AbstractConfiguration {

    public static Configuration read(InputStream inputStream) throws IOException {
        var properties = new Properties();
        properties.load(inputStream);

        List<ScoreDirectorType> enabledScoreDirectorTypes;
        var scoreDirectorTypes = properties.getProperty("score_director_type");
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

        var enabledExamples = parseExamples(properties.getProperty("example"), Example.values());
        var benchmarkProperties = readBenchmarkProperties(properties, getDefault());
        return new Configuration(enabledScoreDirectorTypes, enabledExamples, benchmarkProperties.forkCount(),
                benchmarkProperties.warmupIterations(), benchmarkProperties.measurementIterations(),
                benchmarkProperties.relativeScoreErrorThreshold());
    }

    public static Configuration getDefault() {
        return new Configuration(Arrays.asList(ScoreDirectorType.values()), Arrays.asList(Example.values()), DEFAULT_FORK_COUNT,
                DEFAULT_WARMUP_ITERATIONS, DEFAULT_MEASUREMENT_ITERATIONS, DEFAULT_RELATIVE_SCORE_ERROR_THRESHOLD);
    }

    private final List<ScoreDirectorType> enabledScoreDirectorTypes;
    private final List<Example> enabledExamples;

    private Configuration(List<ScoreDirectorType> enabledScoreDirectorTypes, List<Example> enabledExamples,
            int forkCount, int warmupIterations, int measurementIterations, double relativeScoreErrorThreshold) {
        super(forkCount, warmupIterations, measurementIterations, relativeScoreErrorThreshold);
        this.enabledScoreDirectorTypes = enabledScoreDirectorTypes;
        this.enabledExamples = enabledExamples;
    }

    public List<ScoreDirectorType> getEnabledScoreDirectorTypes() {
        return enabledScoreDirectorTypes;
    }

    public List<Example> getEnabledExamples() {
        return enabledExamples;
    }

}
