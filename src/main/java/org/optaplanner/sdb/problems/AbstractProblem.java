package org.optaplanner.sdb.problems;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import org.optaplanner.core.config.heuristic.selector.common.SelectionCacheType;
import org.optaplanner.core.config.heuristic.selector.common.SelectionOrder;
import org.optaplanner.core.config.heuristic.selector.entity.EntitySelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.MoveSelectorConfig;
import org.optaplanner.core.config.heuristic.selector.move.composite.UnionMoveSelectorConfig;
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
import org.optaplanner.sdb.params.ScoreDirectorType;

abstract class AbstractProblem<Solution_> implements Problem {

    private final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory;
    private final Solution_ originalSolution;
    private final MoveSelectorFactory<Solution_> moveSelectorFactory;

    private InnerScoreDirector<Solution_, ?> scoreDirector;
    private MoveSelector<Solution_> moveSelector;
    private SolverScope<Solution_> solverScope;
    private LocalSearchPhaseScope<Solution_> phaseScope;
    private LocalSearchStepScope<Solution_> stepScope;
    private Move<Solution_> move;

    protected AbstractProblem(final Example example, final ScoreDirectorType scoreDirectorType) {
        final ScoreDirectorFactoryConfig scoreDirectorFactoryConfig =
                buildScoreDirectorFactoryConfig(Objects.requireNonNull(scoreDirectorType));
        scoreDirectorFactory =
                ScoreDirectorType.buildScoreDirectorFactory(scoreDirectorFactoryConfig, buildSolutionDescriptor());
        originalSolution = ProblemInitializer.getSolution(example, scoreDirectorFactory.getSolutionDescriptor(),
                this::buildScoreDirectorFactoryConfig, this::readOriginalSolution); // Expensive.
        moveSelectorFactory = buildMoveSelectorFactory(scoreDirectorFactory.getSolutionDescriptor());
    }

    abstract protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType);

    abstract protected SolutionDescriptor<Solution_> buildSolutionDescriptor();

    abstract protected Solution_ readOriginalSolution();

    protected MoveSelectorFactory<Solution_> buildMoveSelectorFactory(SolutionDescriptor<Solution_> solutionDescriptor) {
        // Create a union move selector over all entities and variables.
        // We go via move config, so we don't have to worry about differences between chained and non-chained problems.
        List<MoveSelectorConfig> moveSelectorConfigs = solutionDescriptor.getGenuineEntityDescriptors().stream()
                .flatMap(entityDescriptor -> {
                            EntitySelectorConfig entitySelectorConfig =
                                    new EntitySelectorConfig(entityDescriptor.getEntityClass());
                            return entityDescriptor.getGenuineVariableDescriptorList().stream()
                                    .map(variableDescriptor -> {
                                        String variableName = variableDescriptor.getVariableName();
                                        ValueSelectorConfig valueSelectorConfig = new ValueSelectorConfig(variableName);
                                        ChangeMoveSelectorConfig moveSelectorConfig = new ChangeMoveSelectorConfig();
                                        moveSelectorConfig.setEntitySelectorConfig(entitySelectorConfig);
                                        moveSelectorConfig.setValueSelectorConfig(valueSelectorConfig);
                                        return moveSelectorConfig;
                                    });
                        }
                ).collect(Collectors.toList());
        UnionMoveSelectorConfig unionMoveSelectorConfig = new UnionMoveSelectorConfig();
        unionMoveSelectorConfig.setMoveSelectorList(moveSelectorConfigs);
        return MoveSelectorFactory.create(unionMoveSelectorConfig);
    }

    @Override
    public final void setupTrial() {
        scoreDirector = scoreDirectorFactory.buildScoreDirector(false, false);
        // Prepare the move selector that will pick different move for each invocation.
        // Reproducible random selection without caching; we need the selection to never end.
        final HeuristicConfigPolicy<Solution_> policy = new HeuristicConfigPolicy.Builder<>(EnvironmentMode.REPRODUCIBLE,
                null, null, null, scoreDirectorFactory)
                .build();
        moveSelector = moveSelectorFactory.buildMoveSelector(policy, SelectionCacheType.JUST_IN_TIME,
                SelectionOrder.RANDOM);
    }

    @Override
    public final void setupIteration() {
        // We only care about incremental performance; therefore calculate the entire solution outside of invocation.
        scoreDirector.setWorkingSolution(scoreDirector.cloneSolution(originalSolution)); // Use fresh solution again.
        scoreDirector.triggerVariableListeners();
        scoreDirector.calculateScore();
        // Prepare the lifecycle.
        solverScope = new SolverScope<>();
        solverScope.setScoreDirector(scoreDirector);
        solverScope.setWorkingRandom(new Random(0)); // Make results reproducible.
        phaseScope = new LocalSearchPhaseScope<>(solverScope);
        moveSelector.solvingStarted(solverScope);
        moveSelector.phaseStarted(phaseScope);
        stepScope = new LocalSearchStepScope<>(phaseScope);
    }

    @Override
    public final void setupInvocation() {
        moveSelector.stepStarted(stepScope);
        Iterator<Move<Solution_>> moveIterator = moveSelector.iterator();
        do {
            move = moveIterator.next(); // Find the next doable move.
        } while (!move.isMoveDoable(scoreDirector));
    }

    @Override
    public final Object runInvocation() {
        // We're benchmarking the actual operations inside the score director:
        // - Speed of variable updates.
        // - Speed of score calculation on those updates.
        // Unfortunately, we also benchmark a bit of the overhead of the move. Hopefully, that is not too much.
        // More importantly, it is a constant overhead and therefore should not affect the results.
        move.doMoveOnly(scoreDirector);
        return scoreDirector.calculateScore(); // Run incremental calculation over the changes made by the move.
    }

    @Override
    public final void tearDownInvocation() {
        move = null;
        moveSelector.stepEnded(stepScope);
    }

    @Override
    public final void tearDownIteration() {
        moveSelector.phaseEnded(phaseScope);
        moveSelector.solvingEnded(solverScope);
    }

    @Override
    public final void teardownTrial() {
        scoreDirector.close();
    }

}
