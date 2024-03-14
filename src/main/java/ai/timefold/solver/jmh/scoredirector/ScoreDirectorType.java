package ai.timefold.solver.jmh.scoredirector;

import java.util.Objects;

import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.core.impl.score.director.InnerScoreDirectorFactory;
import ai.timefold.solver.core.impl.score.director.ScoreDirectorFactoryFactory;
import ai.timefold.solver.jmh.scoredirector.benchmarks.AbstractBenchmark;
import ai.timefold.solver.jmh.scoredirector.benchmarks.ConstraintStreamsBenchmark;
import ai.timefold.solver.jmh.scoredirector.benchmarks.ConstraintStreamsJustifiedBenchmark;
import ai.timefold.solver.jmh.scoredirector.benchmarks.EasyBenchmark;
import ai.timefold.solver.jmh.scoredirector.benchmarks.IncrementalBenchmark;

/**
 * Order by expected speed increase.
 */
public enum ScoreDirectorType implements Comparable<ScoreDirectorType> {

    EASY(EasyBenchmark.class, "easyExample"),
    CONSTRAINT_STREAMS_JUSTIFIED(ConstraintStreamsJustifiedBenchmark.class, "csJustifiedExample"),
    CONSTRAINT_STREAMS(ConstraintStreamsBenchmark.class, "csExample"),
    INCREMENTAL(IncrementalBenchmark.class, "incrementalExample");

    private final Class<? extends AbstractBenchmark> benchmarkClass;
    private final String benchmarkParamName;

    ScoreDirectorType(Class<? extends AbstractBenchmark> benchmarkClass, String benchmarkParamName) {
        this.benchmarkClass = Objects.requireNonNull(benchmarkClass);
        this.benchmarkParamName = benchmarkParamName;
    }

    public Class<? extends AbstractBenchmark> getBenchmarkClass() {
        return benchmarkClass;
    }

    public String getBenchmarkParamName() {
        return benchmarkParamName;
    }

    public static <Solution_> InnerScoreDirectorFactory<Solution_, ?> buildScoreDirectorFactory(
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig, SolutionDescriptor<Solution_> solutionDescriptor) {
        ScoreDirectorFactoryFactory<Solution_, ?> scoreDirectorFactoryFactory =
                new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig);
        return scoreDirectorFactoryFactory.buildScoreDirectorFactory(ScoreDirectorType.class.getClassLoader(),
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor);
    }

}
