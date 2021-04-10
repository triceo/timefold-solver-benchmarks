package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.domain.Room;
import org.optaplanner.examples.curriculumcourse.optional.score.CurriculumCourseConstraintProvider;

import java.util.List;

public final class CurriculumCourseProblem extends AbstractProblem<CourseSchedule, Lecture, Room> {

    public CurriculumCourseProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(CurriculumCourseConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/curriculumcourse/solver/curriculumCourseConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<CourseSchedule> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(CourseSchedule.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "room";
    }

    @Override
    protected CourseSchedule readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<Lecture> getEntities(CourseSchedule courseSchedule) {
        return courseSchedule.getLectureList();
    }

    @Override
    protected Room readValue(Lecture lecture) {
        return lecture.getRoom();
    }

    @Override
    protected void writeValue(Lecture lecture, Room room) {
        lecture.setRoom(room);
    }

}
