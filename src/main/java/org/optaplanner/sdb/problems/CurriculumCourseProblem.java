package org.optaplanner.sdb.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.sdb.params.ScoreDirector;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.domain.Lecture;
import org.optaplanner.examples.curriculumcourse.optional.score.CurriculumCourseConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public final class CurriculumCourseProblem extends AbstractProblem<CourseSchedule, Lecture> {

    public CurriculumCourseProblem(ScoreDirector scoreDirector) {
        super(Example.CURRICULUM_COURSE, scoreDirector);
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
                        .withScoreDrls("org/optaplanner/examples/curriculumcourse/solver/curriculumCourseConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<CourseSchedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(CourseSchedule.class, Lecture.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "room";
    }

    @Override
    protected CourseSchedule readOriginalSolution() {
        final XStreamSolutionFileIO<CourseSchedule> solutionFileIO =
                new XStreamSolutionFileIO<>(CourseSchedule.class);
        return solutionFileIO.read(new File("data/curriculumcourse-comp07.xml"));
    }

    @Override
    protected Class<Lecture> getEntityClass() {
        return null;
    }

}
