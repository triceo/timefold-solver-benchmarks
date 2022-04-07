package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.taskassigning.domain.Employee;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.score.TaskAssigningConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirectorType;

public final class TaskAssigningProblem extends AbstractProblem<TaskAssigningSolution> {

    public TaskAssigningProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TASK_ASSIGNING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TaskAssigningConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TaskAssigningConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/taskassigning/optional/score/taskAssigningConstraints.drl");
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<TaskAssigningSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TaskAssigningSolution.class, Employee.class,
                Task.class);
    }

    @Override
    protected TaskAssigningSolution readOriginalSolution() {
        final XStreamSolutionFileIO<TaskAssigningSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(TaskAssigningSolution.class);
        return solutionFileIO.read(new File("data/taskassigning-500-20.xml"));
    }

}
