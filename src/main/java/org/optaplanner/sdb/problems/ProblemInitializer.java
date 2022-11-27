package org.optaplanner.sdb.problems;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.config.solver.random.RandomType;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.constructionheuristic.DefaultConstructionHeuristicPhase;
import org.optaplanner.core.impl.constructionheuristic.DefaultConstructionHeuristicPhaseFactory;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.heuristic.HeuristicConfigPolicy;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.InnerScoreDirectorFactory;
import org.optaplanner.core.impl.solver.AbstractSolver;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.event.SolverEventSupport;
import org.optaplanner.core.impl.solver.random.DefaultRandomFactory;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecaller;
import org.optaplanner.core.impl.solver.recaller.BestSolutionRecallerFactory;
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.solver.termination.AbstractTermination;
import org.optaplanner.core.impl.solver.termination.BasicPlumbingTermination;
import org.optaplanner.core.impl.solver.termination.Termination;
import org.optaplanner.core.impl.solver.termination.TerminationFactory;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XLS-based examples can not read initialized solution from the file.
 * Therefore we have to initialize the solution ourselves, by running the CH on the fastest possible score director.
 */
public final class ProblemInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemInitializer.class);

    private static final Map<Example, Object> SOLUTIONS = new EnumMap<>(Example.class);

    public static synchronized <Solution_> Solution_ getSolution(Example example,
            SolutionDescriptor<Solution_> solutionDescriptor,
            Function<ScoreDirectorType, ScoreDirectorFactoryConfig> configFunction, Supplier<Solution_> solutionSupplier) {
        final ScoreDirectorType fastestPossibleScoreDirectorType = Arrays.stream(ScoreDirectorType.values())
                .filter(example::isSupportedOn)
                .max(ScoreDirectorType::compareTo)
                .orElseThrow();
        final ScoreDirectorFactoryConfig config = configFunction.apply(fastestPossibleScoreDirectorType);
        final InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory =
                ScoreDirectorType.buildScoreDirectorFactory(config, solutionDescriptor);
        return (Solution_) SOLUTIONS.computeIfAbsent(example,
                e -> initialize(example, solutionSupplier.get(), scoreDirectorFactory));
    }

    private static <Solution_> Solution_ initialize(Example example, Solution_ uninitializedSolution,
            InnerScoreDirectorFactory<Solution_, ?> scoreDirectorFactory) {
        try (final InnerScoreDirector<Solution_, ?> scoreDirector =
                scoreDirectorFactory.buildScoreDirector(false, false)) {
            scoreDirector.setWorkingSolution(uninitializedSolution);
            scoreDirector.triggerVariableListeners();
            Score<?> score = scoreDirector.calculateScore();
            if (score.isSolutionInitialized()) { // No need to do anything.
                LOGGER.info("Example {} already initialized.", example);
                return uninitializedSolution;
            }
            LOGGER.info("Initializing example {}.", example);
            // Configure the construction heuristic.
            ConstructionHeuristicPhaseConfig config = new ConstructionHeuristicPhaseConfig()
                    .withConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT);
            DefaultConstructionHeuristicPhaseFactory<Solution_> factory =
                    new DefaultConstructionHeuristicPhaseFactory<>(config);

            final HeuristicConfigPolicy<Solution_> policy = new HeuristicConfigPolicy.Builder<>(EnvironmentMode.REPRODUCIBLE,
                    null, null, null, scoreDirectorFactory.getInitializingScoreTrend(),
                    scoreDirectorFactory.getSolutionDescriptor())
                            .build();
            BestSolutionRecaller<Solution_> bestSolutionRecaller =
                    BestSolutionRecallerFactory.create().buildBestSolutionRecaller(EnvironmentMode.REPRODUCIBLE);
            bestSolutionRecaller.setSolverEventSupport(new SolverEventSupport<>(null));
            Termination<Solution_> termination = TerminationFactory.<Solution_> create(new TerminationConfig())
                    .buildTermination(policy, new BasicPlumbingTermination<>(false));
            DefaultConstructionHeuristicPhase<Solution_> constructionHeuristicPhase =
                    (DefaultConstructionHeuristicPhase<Solution_>) factory.buildPhase(0, policy, bestSolutionRecaller,
                            termination);

            // Create solver; more or less a mock.
            SolverScope<Solution_> solverScope = new SolverScope<>();
            solverScope.setBestSolution(uninitializedSolution);
            solverScope.setScoreDirector(scoreDirector);
            solverScope.setSolverMetricSet(EnumSet.noneOf(SolverMetric.class));
            solverScope.startingNow();
            AbstractSolver<Solution_> solver = new DefaultSolver<>(EnvironmentMode.REPRODUCIBLE,
                    new DefaultRandomFactory(RandomType.JDK, 0L), bestSolutionRecaller, null,
                    new NoopTermination<>(), List.of(constructionHeuristicPhase), solverScope, null);
            constructionHeuristicPhase.setSolver(solver);

            // Start the construction heuristic.
            bestSolutionRecaller.solvingStarted(solverScope);
            constructionHeuristicPhase.solvingStarted(solverScope);
            constructionHeuristicPhase.solve(solverScope);
            constructionHeuristicPhase.solvingEnded(solverScope);
            bestSolutionRecaller.solvingEnded(solverScope);
            solverScope.endingNow();
            if (!scoreDirector.calculateScore().isSolutionInitialized()) {
                throw new IllegalStateException("Impossible state: uninitialized after the end of CH.");
            }
            LOGGER.info("Example {} initialized.", example);
            return solverScope.getBestSolution();
        }
    }

    private static final class NoopTermination<Solution_> extends AbstractTermination<Solution_> {
        @Override
        public boolean isSolverTerminated(SolverScope<Solution_> solverScope) {
            return false;
        }

        @Override
        public boolean isPhaseTerminated(AbstractPhaseScope<Solution_> phaseScope) {
            return false;
        }

        @Override
        public double calculateSolverTimeGradient(SolverScope<Solution_> solverScope) {
            return 0;
        }

        @Override
        public double calculatePhaseTimeGradient(AbstractPhaseScope<Solution_> phaseScope) {
            return 0;
        }
    }
}
