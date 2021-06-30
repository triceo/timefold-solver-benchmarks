package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.projectjobscheduling.domain.Allocation;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.examples.projectjobscheduling.optional.score.ProjectJobSchedulingConstraintProvider;
import org.optaplanner.examples.projectjobscheduling.score.ProjectJobSchedulingIncrementalScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class ProjectJobSchedulingProblem extends AbstractProblem<Schedule, Allocation> {

    public ProjectJobSchedulingProblem(ScoreDirector scoreDirector) {
        super(Example.PROJECT_JOB_SCHEDULING, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(ProjectJobSchedulingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/projectjobscheduling/solver/projectJobSchedulingConstraints.drl");
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(ProjectJobSchedulingIncrementalScoreCalculator.class);
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<Schedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(Schedule.class, Allocation.class);
    }

    @Override
    protected List<String> getEntityVariableNames() {
        return Arrays.asList("executionMode", "delay");
    }

    @Override
    protected Schedule readOriginalSolution() {
        final XStreamSolutionFileIO<Schedule> solutionFileIO =
                new XStreamSolutionFileIO<>(Schedule.class);
        return solutionFileIO.read(new File("data/projectjobscheduling-B-10.xml"));
    }

    @Override
    protected Class<Allocation> getEntityClass() {
        return Allocation.class;
    }

}
