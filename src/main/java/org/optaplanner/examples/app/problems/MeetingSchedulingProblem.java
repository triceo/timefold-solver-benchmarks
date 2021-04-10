package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.domain.Room;
import org.optaplanner.examples.meetingscheduling.optional.score.MeetingSchedulingConstraintProvider;
import org.optaplanner.examples.meetingscheduling.persistence.MeetingSchedulingXlsxFileIO;

import java.io.File;
import java.util.List;

public final class MeetingSchedulingProblem extends AbstractProblem<MeetingSchedule, MeetingAssignment, Room> {

    public MeetingSchedulingProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
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
                        .withScoreDrls("/org/optaplanner/examples/meetingscheduling/solver/meetingSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<MeetingSchedule> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(MeetingSchedule.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "room";
    }

    @Override
    protected MeetingSchedule readAndInitializeSolution() {
        return new MeetingSchedulingXlsxFileIO()
                .read(new File("data/meetingscheduling-100-320-5.xlsx"));
    }

    @Override
    protected List<MeetingAssignment> getEntities(MeetingSchedule meetingSchedule) {
        return meetingSchedule.getMeetingAssignmentList();
    }

    @Override
    protected Room readValue(MeetingAssignment meetingAssignment) {
        return meetingAssignment.getRoom();
    }

    @Override
    protected void writeValue(MeetingAssignment meetingAssignment, Room room) {
        meetingAssignment.setRoom(room);
    }

}
