package ai.timefold.solver.jmh.scoredirector.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.tennis.domain.TeamAssignment;
import ai.timefold.solver.examples.tennis.domain.TennisSolution;
import ai.timefold.solver.examples.tennis.persistence.TennisSolutionFileIO;
import ai.timefold.solver.examples.tennis.score.TennisConstraintProvider;
import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;

public final class TennisProblem extends AbstractProblem<TennisSolution> {

    public TennisProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TENNIS, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        ScoreDirectorType nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TennisConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
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
