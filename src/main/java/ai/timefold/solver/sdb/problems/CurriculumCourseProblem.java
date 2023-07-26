package ai.timefold.solver.sdb.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.curriculumcourse.domain.CourseSchedule;
import ai.timefold.solver.examples.curriculumcourse.domain.Lecture;
import ai.timefold.solver.examples.curriculumcourse.persistence.CurriculumCourseSolutionFileIO;
import ai.timefold.solver.examples.curriculumcourse.score.CurriculumCourseConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class CurriculumCourseProblem extends AbstractProblem<CourseSchedule> {

    public CurriculumCourseProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.CURRICULUM_COURSE, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        ScoreDirectorType nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(CurriculumCourseConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
    }

    @Override
    protected SolutionDescriptor<CourseSchedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(CourseSchedule.class, Lecture.class);
    }

    @Override
    protected CourseSchedule readOriginalSolution() {
        final SolutionFileIO<CourseSchedule> solutionFileIO = new CurriculumCourseSolutionFileIO();
        return solutionFileIO.read(new File("data/curriculumcourse-comp07.json"));
    }

}
