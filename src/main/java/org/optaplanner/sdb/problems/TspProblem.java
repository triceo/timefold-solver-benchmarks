package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.optional.score.TspEasyScoreCalculator;
import org.optaplanner.examples.tsp.optional.score.TspIncrementalScoreCalculator;
import org.optaplanner.examples.tsp.persistence.TspSolutionFileIO;
import org.optaplanner.examples.tsp.score.TspConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TspProblem extends AbstractProblem<TspSolution> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TspProblem.class);

    public TspProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TSP, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TspConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TspConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(TspEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(TspIncrementalScoreCalculator.class);
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<TspSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TspSolution.class, Visit.class);
    }

    @Override
    protected TspSolution readOriginalSolution() {
        while (true) {
            try {
                final SolutionFileIO<TspSolution> solutionFileIO = new TspSolutionFileIO();
                return solutionFileIO.read(new File("data/tsp-lu980.json"));
            } catch (StackOverflowError error) { // For some reason, deserialization overflows here *once in a while*.
                LOGGER.warn("Jackson's thrown stack overflow, retrying.");
            }
        }
    }

}
