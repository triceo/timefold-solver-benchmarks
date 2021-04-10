package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.Score;
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
        // We only care about incremental performance; therefore calculate the entire solution outside of invocation.
        scoreDirector.setWorkingSolution(solution);
        scoreDirector.triggerVariableListeners();
        final Score<?> score = scoreDirector.calculateScore();
        if (!score.isSolutionInitialized()) { // Construction heuristics are not in scope.
            throw new IllegalStateException("Solution not initialized (" + score + ").");
        }
    }

    @Override
    public final void setupInvocation() {
        final Entity_ entity = pickRandomEntity(random, entityList);
        final VariableDescriptor<Solution_> variableDescriptor = scoreDirectorFactory.getSolutionDescriptor()
                .findVariableDescriptor(entity, getEntityVariableName());
        final Value_ value = (Value_) variableDescriptor.getValue(entity);
        scoreDirector.beforeVariableChanged(variableDescriptor, entity);
        variableDescriptor.setValue(entity, value); // TODO read some new random value
        scoreDirector.afterVariableChanged(variableDescriptor, entity);
        scoreDirector.triggerVariableListeners();
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
        scoreDirector.close();
        scoreDirector = null;
        solution = null;
        entityList = null;
    }

    @Override
    public final void teardownTrial() {
    }
}
