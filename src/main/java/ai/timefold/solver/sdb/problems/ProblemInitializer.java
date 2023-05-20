package ai.timefold.solver.sdb.problems;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import ai.timefold.solver.core.api.score.Score;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import ai.timefold.solver.core.config.constructionheuristic.ConstructionHeuristicType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.config.solver.EnvironmentMode;
import ai.timefold.solver.core.config.solver.monitoring.SolverMetric;
import ai.timefold.solver.core.config.solver.random.RandomType;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import ai.timefold.solver.core.impl.constructionheuristic.DefaultConstructionHeuristicPhase;
import ai.timefold.solver.core.impl.constructionheuristic.DefaultConstructionHeuristicPhaseFactory;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.core.impl.heuristic.HeuristicConfigPolicy;
import ai.timefold.solver.core.impl.phase.scope.AbstractPhaseScope;
import ai.timefold.solver.core.impl.score.director.InnerScoreDirector;
import ai.timefold.solver.core.impl.score.director.InnerScoreDirectorFactory;
import ai.timefold.solver.core.impl.solver.AbstractSolver;
import ai.timefold.solver.core.impl.solver.ClassInstanceCache;
import ai.timefold.solver.core.impl.solver.DefaultSolver;
import ai.timefold.solver.core.impl.solver.event.SolverEventSupport;
import ai.timefold.solver.core.impl.solver.random.DefaultRandomFactory;
import ai.timefold.solver.core.impl.solver.recaller.BestSolutionRecaller;
import ai.timefold.solver.core.impl.solver.recaller.BestSolutionRecallerFactory;
import ai.timefold.solver.core.impl.solver.scope.SolverScope;
import ai.timefold.solver.core.impl.solver.termination.AbstractTermination;
import ai.timefold.solver.core.impl.solver.termination.BasicPlumbingTermination;
import ai.timefold.solver.core.impl.solver.termination.Termination;
import ai.timefold.solver.core.impl.solver.termination.TerminationFactory;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

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
                    scoreDirectorFactory.getSolutionDescriptor(), ClassInstanceCache.create())
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
