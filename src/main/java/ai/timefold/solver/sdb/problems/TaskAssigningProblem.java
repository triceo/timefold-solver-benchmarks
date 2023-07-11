package ai.timefold.solver.sdb.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.taskassigning.domain.Employee;
import ai.timefold.solver.examples.taskassigning.domain.Task;
import ai.timefold.solver.examples.taskassigning.domain.TaskAssigningSolution;
import ai.timefold.solver.examples.taskassigning.persistence.TaskAssigningSolutionFileIO;
import ai.timefold.solver.examples.taskassigning.score.TaskAssigningConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class TaskAssigningProblem extends AbstractProblem<TaskAssigningSolution> {

    public TaskAssigningProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TASK_ASSIGNING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        ScoreDirectorType nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_BAVET
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_BAVET_JUSTIFIED) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TaskAssigningConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
    }

    @Override
    protected SolutionDescriptor<TaskAssigningSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TaskAssigningSolution.class, Employee.class, Task.class);
    }

    @Override
    protected TaskAssigningSolution readOriginalSolution() {
        final SolutionFileIO<TaskAssigningSolution> solutionFileIO = new TaskAssigningSolutionFileIO();
        return solutionFileIO.read(new File("data/taskassigning-500-20.json"));
    }

}
