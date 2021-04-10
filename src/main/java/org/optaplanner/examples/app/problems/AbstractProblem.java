package org.optaplanner.examples.app.problems;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.value.ValueSelectorConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelector;
import org.optaplanner.core.impl.heuristic.selector.move.MoveSelectorFactory;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.examples.app.params.Example;
import org.optaplanner.examples.app.params.ScoreDirector;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

abstract class AbstractProblem<Solution_, Entity_, Value_> implements Problem {

    private final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory;
    private final Solution_ originalSolution;
    private final MoveSelectorFactory<Solution_> moveSelectorFactory;

    private InnerScoreDirector<Solution_, ?> scoreDirector;
    private Solution_ solution;
    private MoveSelector<Solution_> moveSelector;
    private Iterator<Move<Solution_>> moveIterator;
    private SolverScope<Solution_> solverScope;
    private LocalSearchPhaseScope<Solution_> phaseScope;
    private LocalSearchStepScope<Solution_> stepScope;

    protected AbstractProblem(final Example example, final ScoreDirector scoreDirector) {
        final ScoreDirectorFactoryConfig scoreDirectorFactoryConfig =
                buildScoreDirectorFactoryConfig(Objects.requireNonNull(scoreDirector));
        scoreDirectorFactory =
                ScoreDirector.buildScoreDirectorFactory(scoreDirectorFactoryConfig, buildSolutionDescriptor());
        originalSolution = ProblemInitializer.getSolution(example, scoreDirectorFactory.getSolutionDescriptor(),
                this::buildScoreDirectorFactoryConfig, this::readOriginalSolution); // Expensive.
        moveSelectorFactory = buildMoveSelectorFactory();
    }

    abstract protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector);

    abstract protected SolutionDescriptor<Solution_> buildSolutionDescriptor();

    abstract protected String getEntityVariableName();

    abstract protected Solution_ readOriginalSolution();

    abstract protected Class<Entity_> getEntityClass();

    protected MoveSelectorFactory<Solution_> buildMoveSelectorFactory() {
        EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig(getEntityClass());
        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig(getEntityVariableName());

        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
        moveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
        return MoveSelectorFactory.create(moveSelectorConfig);
    }

    @Override
    public final void setupTrial() {
        solution = readOriginalSolution();
    }

    @Override
    public final void setupIteration() {
        scoreDirector = scoreDirectorFactory.buildScoreDirector(false, false);
        solution = scoreDirector.cloneSolution(originalSolution); // Start with the fresh solution again.
        // We only care about incremental performance; therefore calculate the entire solution outside of invocation.
        scoreDirector.setWorkingSolution(solution);
        scoreDirector.triggerVariableListeners();
        scoreDirector.calculateScore();
        // Prepare the move selector that will pick different move for each invocation.
        solverScope = new SolverScope<>();
        solverScope.setScoreDirector(scoreDirector);
        solverScope.setWorkingRandom(new Random(0)); // Always measure the same thing.
        phaseScope = new LocalSearchPhaseScope<>(solverScope);
        HeuristicConfigPolicy<Solution_> policy = new HeuristicConfigPolicy<>(EnvironmentMode.REPRODUCIBLE, null,
                null, null, scoreDirectorFactory);
        moveSelector = moveSelectorFactory.buildMoveSelector(policy, SelectionCacheType.PHASE, SelectionOrder.RANDOM);
        moveSelector.solvingStarted(solverScope);
        moveSelector.phaseStarted(phaseScope);
        moveIterator = moveSelector.iterator();
    }

    @Override
    public final void setupInvocation() {
        stepScope = new LocalSearchStepScope<>(phaseScope);
        moveSelector.stepStarted(stepScope);
        Move<Solution_> move;
        do {
            move = moveIterator.next();
        } while (!move.isMoveDoable(scoreDirector));
        move.doMove(scoreDirector);
    }

    @Override
    public final Object runInvocation() {
        return scoreDirector.calculateScore();
    }

    @Override
    public final void tearDownInvocation() {
        moveSelector.stepEnded(stepScope);
        stepScope = null;
    }

    @Override
    public final void tearDownIteration() {
        scoreDirector.close();
        scoreDirector = null;
        solution = null;
        moveIterator = null;
        moveSelector.phaseEnded(phaseScope);
        phaseScope = null;
        moveSelector.solvingEnded(solverScope);
        solverScope = null;
        moveSelector = null;
    }

    @Override
    public final void teardownTrial() {
    }

}
