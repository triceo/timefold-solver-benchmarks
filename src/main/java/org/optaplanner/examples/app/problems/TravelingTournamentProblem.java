package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.optional.score.TravelingTournamentConstraintProvider;

import java.util.List;

public final class TravelingTournamentProblem extends AbstractProblem<TravelingTournament, Match, Day> {

    public TravelingTournamentProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TravelingTournamentConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/travelingtournament/solver/travelingTournamentConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<TravelingTournament> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(TravelingTournament.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "day";
    }

    @Override
    protected TravelingTournament readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<Match> getEntities(TravelingTournament travelingTournament) {
        return travelingTournament.getMatchList();
    }

    @Override
    protected Day readValue(Match match) {
        return match.getDay();
    }

    @Override
    protected void writeValue(Match match, Day day) {
        match.setDay(day);
    }

}
