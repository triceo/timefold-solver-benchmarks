package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.optional.score.MeetingSchedulingConstraintProvider;
import org.optaplanner.examples.meetingscheduling.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class MeetingSchedulingProblem extends AbstractProblem<MeetingSchedule, MeetingAssignment> {

    public MeetingSchedulingProblem(ScoreDirector scoreDirector) {
        super(Example.MEETING_SCHEDULING, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(MeetingSchedulingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/meetingscheduling/solver/meetingSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<MeetingSchedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(MeetingSchedule.class, MeetingAssignment.class);
    }

    @Override
    protected List<String> getEntityVariableNames() {
        return Collections.singletonList("room");
    }

    @Override
    protected MeetingSchedule readOriginalSolution() {
        return new MeetingSchedulingXlsxFileIO()
                .read(new File("data/meetingscheduling-100-320-5.xlsx"));
    }

    @Override
    protected Class<MeetingAssignment> getEntityClass() {
        return MeetingAssignment.class;
    }

}
