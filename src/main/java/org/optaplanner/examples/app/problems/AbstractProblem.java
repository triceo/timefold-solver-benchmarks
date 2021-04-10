package org.optaplanner.examples.app.problems;

import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.domain.variable.descriptor.VariableDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.examples.app.directors.ScoreDirector;

import java.util.List;
import java.util.Objects;
import java.util.Random;

abstract class AbstractProblem<Solution_, Entity_, Value_> implements Problem {

    private final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory;
    private final Solution_ originalSolution;

    private InnerScoreDirector<Solution_, ?> scoreDirector;
    private Random random;
    private Solution_ solution;
    private List<Entity_> entityList;

    protected AbstractProblem(final ScoreDirector scoreDirector) {
        final ScoreDirectorFactoryConfig scoreDirectorFactoryConfig =
                buildScoreDirectorFactoryConfig(Objects.requireNonNull(scoreDirector));
        scoreDirectorFactory =
                ScoreDirector.buildScoreDirectorFactory(scoreDirectorFactoryConfig, buildSolutionDescriptor());
        originalSolution = readAndInitializeSolution(); // Expensive; cache this.
    }

    abstract protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector);

    abstract protected SolutionDescriptor<Solution_> buildSolutionDescriptor();

    abstract protected String getEntityVariableName();

    abstract protected Solution_ readAndInitializeSolution();

    abstract protected List<Entity_> getEntities(Solution_ solution);

    private static <Entity_> Entity_ pickRandomEntity(final Random random, final List<Entity_> entities) {
        final int entityCount = entities.size();
        final int randomEntityId = random.nextInt(entityCount);
        return entities.get(randomEntityId);
    }

    abstract protected Value_ readValue(Entity_ entity);

    abstract protected void writeValue(Entity_ entity, Value_ value_);

    @Override
    public final void setupTrial() {
        solution = readAndInitializeSolution();
    }

    @Override
    public final void setupIteration() {
        random = new Random(0); // Always measure the same thing.
        scoreDirector = scoreDirectorFactory.buildScoreDirector(false, false);
        solution = scoreDirector.cloneSolution(originalSolution); // Start with the fresh solution again.
        entityList = getEntities(solution);
        scoreDirector.setWorkingSolution(solution);
    }

    @Override
    public final void setupInvocation() {
        final Entity_ leftEntity = pickRandomEntity(random, entityList);
        final Entity_ rightEntity = pickRandomEntity(random, entityList);
        // We don't want to run any variable listeners inside the invocation.
        final Value_ leftValue = readValue(leftEntity);
        final Value_ rightValue = readValue(rightEntity);
        final VariableDescriptor<Solution_> variableDescriptor = scoreDirectorFactory.getSolutionDescriptor()
                .findVariableDescriptor(leftEntity, getEntityVariableName());
        scoreDirector.beforeVariableChanged(variableDescriptor, leftEntity);
        writeValue(leftEntity, rightValue);
        scoreDirector.afterVariableChanged(variableDescriptor, leftEntity);
        scoreDirector.beforeVariableChanged(variableDescriptor, rightEntity);
        writeValue(rightEntity, leftValue);
        scoreDirector.afterVariableChanged(variableDescriptor, rightEntity);
    }

    @Override
    public final Object runInvocation() {
        return scoreDirector.calculateScore();
    }

    @Override
    public final void tearDownInvocation() {
    }

    @Override
    public final void tearDownIteration() {
        random = null;
        scoreDirector = null;
        solution = null;
        entityList = null;
    }

    @Override
    public final void teardownTrial() {
        scoreDirector.close();
        scoreDirector = null;
    }
}
