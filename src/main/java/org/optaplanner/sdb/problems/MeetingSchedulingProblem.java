package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.meetingscheduling.domain.MeetingAssignment;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;
import org.optaplanner.examples.meetingscheduling.persistence.MeetingSchedulingXlsxFileIO;
import org.optaplanner.examples.meetingscheduling.score.MeetingSchedulingConstraintProvider;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirectorType;

public final class MeetingSchedulingProblem extends AbstractProblem<MeetingSchedule> {

    public MeetingSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.MEETING_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(MeetingSchedulingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/meetingscheduling/optional/score/meetingSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<MeetingSchedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(MeetingSchedule.class, MeetingAssignment.class);
    }

    @Override
    protected MeetingSchedule readOriginalSolution() {
        return new MeetingSchedulingXlsxFileIO()
                .read(new File("data/meetingscheduling-100-320-5.xlsx"));
    }

}
