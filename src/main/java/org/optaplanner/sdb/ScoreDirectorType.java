package org.optaplanner.sdb;

import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactory;

/**
 * Order by expected speed increase.
 */
public enum ScoreDirectorType implements Comparable<ScoreDirectorType> {

    JAVA_EASY,
    CONSTRAINT_STREAMS_DROOLS,
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
