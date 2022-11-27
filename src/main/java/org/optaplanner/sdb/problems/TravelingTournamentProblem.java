package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentSolutionFileIO;
import org.optaplanner.examples.travelingtournament.score.TravelingTournamentConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class TravelingTournamentProblem extends AbstractProblem<TravelingTournament> {

    public TravelingTournamentProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TRAVELING_TOURNAMENT, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TravelingTournamentConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TravelingTournamentConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL -> scoreDirectorFactoryConfig
                    .withScoreDrls("org/optaplanner/examples/travelingtournament/optional/score/travelingTournamentConstraints.drl");
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
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
