package ai.timefold.solver.sdb.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.travelingtournament.domain.Match;
import ai.timefold.solver.examples.travelingtournament.domain.TravelingTournament;
import ai.timefold.solver.examples.travelingtournament.persistence.TravelingTournamentSolutionFileIO;
import ai.timefold.solver.examples.travelingtournament.score.TravelingTournamentConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class TravelingTournamentProblem extends AbstractProblem<TravelingTournament> {

    public TravelingTournamentProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TRAVELING_TOURNAMENT, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        ScoreDirectorType nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_BAVET
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_BAVET_JUSTIFIED) {
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
