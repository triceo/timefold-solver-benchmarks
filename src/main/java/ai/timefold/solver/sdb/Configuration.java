package ai.timefold.solver.sdb;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

final class Configuration {

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
        double relativeScoreErrorThreshold;
        try {
            relativeScoreErrorThreshold = Double.parseDouble(properties.getProperty("relative_score_error_threshold", "0.02"));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed parsing relative_score_error_threshold.", ex);
        }
        return new Configuration(enabledScoreDirectorTypes, enabledExamples, relativeScoreErrorThreshold);
    }

    public static Configuration getDefault() {
        return new Configuration(Arrays.asList(ScoreDirectorType.values()), Arrays.asList(Example.values()), 0.02);
    }

    private final List<ScoreDirectorType> enabledScoreDirectorTypes;
    private final List<Example> enabledExamples;
    private final double relativeScoreErrorThreshold;

    private Configuration(List<ScoreDirectorType> enabledScoreDirectorTypes, List<Example> enabledExamples, double relativeScoreErrorThreshold) {
        this.enabledScoreDirectorTypes = enabledScoreDirectorTypes;
        this.enabledExamples = enabledExamples;
        this.relativeScoreErrorThreshold = relativeScoreErrorThreshold;
    }

    public List<ScoreDirectorType> getEnabledScoreDirectorTypes() {
        return enabledScoreDirectorTypes;
    }

    public List<Example> getEnabledExamples() {
        return enabledExamples;
    }

    public double getRelativeScoreErrorThreshold() {
        return relativeScoreErrorThreshold;
    }
}
