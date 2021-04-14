package org.optaplanner.sdb.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.sdb.params.ScoreDirector;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.optional.score.TravelingTournamentConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public final class TravelingTournamentProblem extends AbstractProblem<TravelingTournament, Match> {

    public TravelingTournamentProblem(ScoreDirector scoreDirector) {
        super(Example.TRAVELING_TOURNAMENT, scoreDirector);
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
                        .withScoreDrls("org/optaplanner/examples/travelingtournament/solver/travelingTournamentConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<TravelingTournament> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TravelingTournament.class, Match.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "day";
    }

    @Override
    protected TravelingTournament readOriginalSolution() {
        final XStreamSolutionFileIO<TravelingTournament> solutionFileIO =
                new XStreamSolutionFileIO<>(TravelingTournament.class);
        return solutionFileIO.read(new File("data/travelingtournament-4-super14.xml"));
    }

    @Override
    protected Class<Match> getEntityClass() {
        return Match.class;
    }

}
