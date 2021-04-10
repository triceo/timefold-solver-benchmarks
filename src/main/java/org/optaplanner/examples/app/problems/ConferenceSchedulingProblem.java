package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.optional.score.ConferenceSchedulingConstraintProvider;

import java.util.List;

public final class ConferenceSchedulingProblem extends AbstractProblem<ConferenceSolution, Talk, Room> {

    public ConferenceSchedulingProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(ConferenceSchedulingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/conferencescheduling/solver/conferenceSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<ConferenceSolution> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(ConferenceSolution.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "room";
    }

    @Override
    protected ConferenceSolution readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<Talk> getEntities(ConferenceSolution conferenceSolution) {
        return conferenceSolution.getTalkList();
    }

    @Override
    protected Room readValue(Talk talk) {
        return talk.getRoom();
    }

    @Override
    protected void writeValue(Talk talk, Room room) {
        talk.setRoom(room);
    }

}
