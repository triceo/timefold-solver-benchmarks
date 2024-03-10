package ai.timefold.solver.jmh.scoredirector.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.travelingtournament.domain.Match;
import ai.timefold.solver.examples.travelingtournament.domain.TravelingTournament;
import ai.timefold.solver.examples.travelingtournament.persistence.TravelingTournamentSolutionFileIO;
import ai.timefold.solver.examples.travelingtournament.score.TravelingTournamentConstraintProvider;
import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;

public final class TravelingTournamentProblem extends AbstractProblem<TravelingTournament> {

    public TravelingTournamentProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TRAVELING_TOURNAMENT, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        var scoreDirectorFactoryConfig = buildInitialScoreDirectorFactoryConfig();
        var nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TravelingTournamentConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
    }

    @Override
    protected SolutionDescriptor<TravelingTournament> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TravelingTournament.class, Match.class);
    }

    @Override
    protected TravelingTournament readOriginalSolution() {
        final SolutionFileIO<TravelingTournament> solutionFileIO = new TravelingTournamentSolutionFileIO();
        return solutionFileIO.read(new File("data/travelingtournament-4-super14.json"));
    }

}
