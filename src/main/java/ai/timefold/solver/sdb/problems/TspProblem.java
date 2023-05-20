package ai.timefold.solver.sdb.problems;

import java.io.File;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.tsp.domain.TspSolution;
import ai.timefold.solver.examples.tsp.domain.Visit;
import ai.timefold.solver.examples.tsp.optional.score.TspEasyScoreCalculator;
import ai.timefold.solver.examples.tsp.optional.score.TspIncrementalScoreCalculator;
import ai.timefold.solver.examples.tsp.persistence.TspSolutionFileIO;
import ai.timefold.solver.examples.tsp.score.TspConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TspProblem extends AbstractProblem<TspSolution> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TspProblem.class);

    public TspProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.TSP, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(TspConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case JAVA_EASY -> scoreDirectorFactoryConfig
                    .withEasyScoreCalculatorClass(TspEasyScoreCalculator.class);
            case JAVA_INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(TspIncrementalScoreCalculator.class);
        };
    }

    @Override
    protected SolutionDescriptor<TspSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TspSolution.class, Visit.class);
    }

    @Override
    protected TspSolution readOriginalSolution() {
        while (true) {
            try {
                final SolutionFileIO<TspSolution> solutionFileIO = new TspSolutionFileIO();
                return solutionFileIO.read(new File("data/tsp-lu980.json"));
            } catch (StackOverflowError error) { // For some reason, deserialization overflows here *once in a while*.
                LOGGER.warn("Jackson's thrown stack overflow, retrying.");
            }
        }
    }

}
