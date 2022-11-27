package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.projectjobscheduling.domain.Allocation;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.examples.projectjobscheduling.optional.score.ProjectJobSchedulingIncrementalScoreCalculator;
import org.optaplanner.examples.projectjobscheduling.persistence.ProjectJobSchedulingSolutionFileIO;
import org.optaplanner.examples.projectjobscheduling.score.ProjectJobSchedulingConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class ProjectJobSchedulingProblem extends AbstractProblem<Schedule> {

    public ProjectJobSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.PROJECT_JOB_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(ProjectJobSchedulingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(ProjectJobSchedulingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL -> scoreDirectorFactoryConfig
                    .withScoreDrls(
                            "org/optaplanner/examples/projectjobscheduling/optional/score/projectJobSchedulingConstraints.drl");
            case JAVA_INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(ProjectJobSchedulingIncrementalScoreCalculator.class);
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
    }

    @Override
    protected SolutionDescriptor<Schedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(Schedule.class, Allocation.class);
    }

    @Override
    protected Schedule readOriginalSolution() {
        final SolutionFileIO<Schedule> solutionFileIO = new ProjectJobSchedulingSolutionFileIO();
        return solutionFileIO.read(new File("data/projectjobscheduling-B-7.json"));
    }

}
