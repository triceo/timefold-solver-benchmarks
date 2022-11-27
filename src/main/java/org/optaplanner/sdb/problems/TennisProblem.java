package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.persistence.TennisSolutionFileIO;
import org.optaplanner.examples.tennis.score.TennisConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class TennisProblem extends AbstractProblem<TennisSolution> {

    public TennisProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TENNIS, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TennisConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TennisConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL -> scoreDirectorFactoryConfig
                    .withScoreDrls("org/optaplanner/examples/tennis/optional/score/tennisConstraints.drl");
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
    }

    @Override
    protected SolutionDescriptor<TennisSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TennisSolution.class, TeamAssignment.class);
    }

    @Override
    protected TennisSolution readOriginalSolution() {
        final SolutionFileIO<TennisSolution> solutionFileIO = new TennisSolutionFileIO();
        return solutionFileIO.read(new File("data/tennis-munich-7teams.json"));
    }

}
