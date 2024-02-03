package ai.timefold.solver.jmh.scoredirector.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.meetingscheduling.domain.MeetingAssignment;
import ai.timefold.solver.examples.meetingscheduling.domain.MeetingSchedule;
import ai.timefold.solver.examples.meetingscheduling.persistence.MeetingSchedulingXlsxFileIO;
import ai.timefold.solver.examples.meetingscheduling.score.MeetingSchedulingConstraintProvider;
import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;

public final class MeetingSchedulingProblem extends AbstractProblem<MeetingSchedule> {

    public MeetingSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.MEETING_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        ScoreDirectorType nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(MeetingSchedulingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
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
