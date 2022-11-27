package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.persistence.CurriculumCourseSolutionFileIO;
import org.optaplanner.examples.curriculumcourse.score.CurriculumCourseConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class CurriculumCourseProblem extends AbstractProblem<CourseSchedule> {

    public CurriculumCourseProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.CURRICULUM_COURSE, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(CurriculumCourseConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(CurriculumCourseConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL -> scoreDirectorFactoryConfig
                    .withScoreDrls("org/optaplanner/examples/curriculumcourse/optional/score/curriculumCourseConstraints.drl");
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
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
