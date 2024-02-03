package ai.timefold.solver.jmh.coldstart;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import ai.timefold.solver.jmh.coldstart.problems.Example;
import ai.timefold.solver.jmh.common.AbstractConfiguration;

final class Configuration extends AbstractConfiguration {

    public static Configuration read(InputStream inputStream) throws IOException {
        var properties = new Properties();
        properties.load(inputStream);

        var enabledExamples = parseExamples(properties.getProperty("examples"), Example.values());
        var benchmarkProperties = readBenchmarkProperties(properties, getDefault());
        return new Configuration(enabledExamples, benchmarkProperties.forkCount(), benchmarkProperties.warmupIterations(),
                benchmarkProperties.measurementIterations(), benchmarkProperties.relativeScoreErrorThreshold());
    }

    public static Configuration getDefault() {
        return new Configuration(Arrays.asList(Example.values()), 80, 0, 1, 0.04);
    }

    private final List<Example> enabledExamples;

    private Configuration(List<Example> enabledExamples, int forkCount, int warmupIterations, int measurementIterations,
            double relativeScoreErrorThreshold) {
        super(forkCount, warmupIterations, measurementIterations, relativeScoreErrorThreshold);
        this.enabledExamples = enabledExamples;
    }

    public List<Example> getEnabledExamples() {
        return enabledExamples;
    }

}
