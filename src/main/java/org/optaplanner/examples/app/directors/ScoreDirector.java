package org.optaplanner.examples.app.directors;

import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryFactory;

public enum ScoreDirector {

    DRL,
    CONSTRAINT_STREAMS_BAVET,
    CONSTRAINT_STREAMS_DROOLS,
    JAVA_EASY,
    JAVA_INCREMENTAL;

    public static <Solution_> InnerScoreDirectorFactory<Solution_, ?> buildScoreDirectorFactory(
            ScoreDirectorFactoryConfig scoreDirectorFactoryConfig, SolutionDescriptor<Solution_> solutionDescriptor) {
        ScoreDirectorFactoryFactory<Solution_, ?> scoreDirectorFactoryFactory =
                new ScoreDirectorFactoryFactory<>(scoreDirectorFactoryConfig);
        return scoreDirectorFactoryFactory.buildScoreDirectorFactory(ScoreDirector.class.getClassLoader(),
                EnvironmentMode.REPRODUCIBLE, solutionDescriptor);
    }

}
