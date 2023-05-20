package ai.timefold.solver.sdb;

import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.core.impl.score.director.InnerScoreDirectorFactory;
import ai.timefold.solver.core.impl.score.director.ScoreDirectorFactoryFactory;

/**
 * Order by expected speed increase.
 */
public enum ScoreDirectorType implements Comparable<ScoreDirectorType> {

    JAVA_EASY,
    CONSTRAINT_STREAMS_BAVET,
    JAVA_INCREMENTAL;

    public static <Solution_> InnerScoreDirectorFactory<Solution_, ?> buildScoreDirectorFactory(
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig, SolutionDescriptor<Solution_> solutionDescriptor) {
        ScoreDirectorFactoryFactory<Solution_, ?> scoreDirectorFactoryFactory =
                new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig);
        return scoreDirectorFactoryFactory.buildScoreDirectorFactory(ScoreDirectorType.class.getClassLoader(),
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor);
    }



}
