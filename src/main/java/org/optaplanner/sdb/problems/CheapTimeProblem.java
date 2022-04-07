package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.optional.score.CheapTimeEasyScoreCalculator;
import org.optaplanner.examples.cheaptime.optional.score.CheapTimeIncrementalScoreCalculator;
import org.optaplanner.examples.cheaptime.score.CheapTimeConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirectorType;

public final class CheapTimeProblem extends AbstractProblem<CheapTimeSolution> {

    public CheapTimeProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.CHEAP_TIME, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(CheapTimeConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(CheapTimeConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/cheaptime/optional/score/cheapTimeConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(CheapTimeEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(CheapTimeIncrementalScoreCalculator.class);
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<CheapTimeSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(CheapTimeSolution.class, TaskAssignment.class);
    }

    @Override
    protected CheapTimeSolution readOriginalSolution() {
        final XStreamSolutionFileIO<CheapTimeSolution> solutionFileIO = new XStreamSolutionFileIO<>(CheapTimeSolution.class);
        return solutionFileIO.read(new File("data/cheaptime-instance03.xml"));
    }

}
