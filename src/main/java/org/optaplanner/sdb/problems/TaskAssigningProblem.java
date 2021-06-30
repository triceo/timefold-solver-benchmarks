package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.taskassigning.domain.Task;
import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.examples.taskassigning.domain.TaskOrEmployee;
import org.optaplanner.examples.taskassigning.optional.score.TaskAssigningConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class TaskAssigningProblem extends AbstractProblem<TaskAssigningSolution, Task> {

    public TaskAssigningProblem(ScoreDirector scoreDirector) {
        super(Example.TASK_ASSIGNING, scoreDirector);
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
                        .withScoreDrls("org/optaplanner/examples/taskassigning/solver/taskAssigningConstraints.drl");
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<TaskAssigningSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TaskAssigningSolution.class, TaskOrEmployee.class,
                Task.class);
    }

    @Override
    protected List<String> getEntityVariableNames() {
        return Collections.singletonList("previousTaskOrEmployee");
    }

    @Override
    protected TaskAssigningSolution readOriginalSolution() {
        final XStreamSolutionFileIO<TaskAssigningSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(TaskAssigningSolution.class);
        return solutionFileIO.read(new File("data/taskassigning-500-20.xml"));
    }

    @Override
    protected Class<Task> getEntityClass() {
        return Task.class;
    }

}
