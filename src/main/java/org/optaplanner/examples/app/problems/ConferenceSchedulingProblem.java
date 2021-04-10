package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.params.ScoreDirector;
import org.optaplanner.examples.app.params.Example;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.optional.score.ConferenceSchedulingConstraintProvider;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;

import java.io.File;

public final class ConferenceSchedulingProblem extends AbstractProblem<ConferenceSolution, Talk, Room> {

    public ConferenceSchedulingProblem(ScoreDirector scoreDirector) {
        super(Example.CONFERENCE_SCHEDULING, scoreDirector);
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
                        .withScoreDrls("org/optaplanner/examples/conferencescheduling/solver/conferenceSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<ConferenceSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(ConferenceSolution.class, Talk.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "room";
    }

    @Override
    protected ConferenceSolution readOriginalSolution() {
        return new ConferenceSchedulingXlsxFileIO()
                .read(new File("data/conferencescheduling-216-18-20.xlsx"));
    }

    @Override
    protected Class<Talk> getEntityClass() {
        return Talk.class;
    }

}
