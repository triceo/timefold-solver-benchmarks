package org.optaplanner.sdb.problems;

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
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

abstract class AbstractProblem<Solution_, Entity_> implements Problem {

    private final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory;
    private final Solution_ originalSolution;
    private final MoveSelectorFactory<Solution_> moveSelectorFactory;

    private InnerScoreDirector<Solution_, ?> scoreDirector;
    private MoveSelector<Solution_> moveSelector;
    private Iterator<Move<Solution_>> moveIterator;
    private SolverScope<Solution_> solverScope;
    private LocalSearchPhaseScope<Solution_> phaseScope;
    private LocalSearchStepScope<Solution_> stepScope;
    private Move<Solution_> move;

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
        final EntitySelectorConfig entitySelectorConfig = new EntitySelectorConfig(getEntityClass());
        final ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig(getEntityVariableName());
        final ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
        moveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
        moveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
        return MoveSelectorFactory.create(moveSelectorConfig);
    }

    @Override
    public final void setupTrial() {
        // No need to do anything.
    }

    @Override
    public final void setupIteration() {
        scoreDirector = scoreDirectorFactory.buildScoreDirector(false, false);
        // We only care about incremental performance; therefore calculate the entire solution outside of invocation.
        scoreDirector.setWorkingSolution(scoreDirector.cloneSolution(originalSolution)); // Use fresh solution again.
        scoreDirector.triggerVariableListeners();
        scoreDirector.calculateScore();
        // Prepare the move selector that will pick different move for each invocation.
        solverScope = new SolverScope<>();
        solverScope.setScoreDirector(scoreDirector);
        solverScope.setWorkingRandom(new Random(0)); // Make results predictable.
        phaseScope = new LocalSearchPhaseScope<>(solverScope);
        final HeuristicConfigPolicy<Solution_> policy = new HeuristicConfigPolicy<>(EnvironmentMode.REPRODUCIBLE,
                null, null, null, scoreDirectorFactory);
        moveSelector = moveSelectorFactory.buildMoveSelector(policy, SelectionCacheType.JUST_IN_TIME,
                SelectionOrder.RANDOM); // Random selection, as we need the iterator to never end.
        moveSelector.solvingStarted(solverScope);
        moveSelector.phaseStarted(phaseScope);
        stepScope = new LocalSearchStepScope<>(phaseScope);
        moveSelector.stepStarted(stepScope);
        moveIterator = moveSelector.iterator();
    }

    @Override
    public final void setupInvocation() {
        do {
            move = moveIterator.next();
        } while (!move.isMoveDoable(scoreDirector));
    }

    @Override
    public final Object runInvocation() {
        // We're benchmarking the actual operations inside the score director:
        // - Speed of variable updates.
        // - Speed of score calculation on those updates.
        // Unfortunately, we also benchmark a bit of the overhead of the move. Hopefully, that is not too much.
        // More importantly, it is a constant overhead and therefore should not affect the results.
        move.doMove(scoreDirector);
        return scoreDirector.calculateScore();
    }

    @Override
    public final void tearDownInvocation() {
        // No need to do anything.
    }

    @Override
    public final void tearDownIteration() {
        moveSelector.stepEnded(stepScope);
        moveSelector.phaseEnded(phaseScope);
        moveSelector.solvingEnded(solverScope);
        scoreDirector.close();
    }

    @Override
    public final void teardownTrial() {
        // No need to do anything.
    }

}
