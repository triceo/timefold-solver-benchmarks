package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskOrEmployee;
import org.optaplanner.examples.taskassigning.optional.score.TaskAssigningConstraintProvider;

import java.util.List;

public final class TaskAssigningProblem extends AbstractProblem<TaskAssigningSolution, Task, TaskOrEmployee> {

    public TaskAssigningProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
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
                        .withScoreDrls("/org/optaplanner/examples/taskassigning/solver/taskAssigningConstraints.drl");
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<TaskAssigningSolution> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(TaskAssigningSolution.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "previousTaskOrEmployee";
    }

    @Override
    protected TaskAssigningSolution readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<Task> getEntities(TaskAssigningSolution taskAssigningSolution) {
        return taskAssigningSolution.getTaskList();
    }

    @Override
    protected TaskOrEmployee readValue(Task task) {
        return task.getPreviousTaskOrEmployee();
    }

    @Override
    protected void writeValue(Task task, TaskOrEmployee taskOrEmployee) {
        task.setPreviousTaskOrEmployee(taskOrEmployee);
    }

}
