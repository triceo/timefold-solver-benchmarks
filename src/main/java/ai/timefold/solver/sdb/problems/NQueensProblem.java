package ai.timefold.solver.sdb.problems;

import java.io.File;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.nqueens.domain.NQueens;
import ai.timefold.solver.examples.nqueens.domain.Queen;
import ai.timefold.solver.examples.nqueens.optional.score.NQueensAdvancedIncrementalScoreCalculator;
import ai.timefold.solver.examples.nqueens.optional.score.NQueensMapBasedEasyScoreCalculator;
import ai.timefold.solver.examples.nqueens.persistence.NQueensSolutionFileIO;
import ai.timefold.solver.examples.nqueens.score.NQueensConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class NQueensProblem extends AbstractProblem<NQueens> {

    public NQueensProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.NQUEENS, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(NQueensConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case JAVA_EASY -> scoreDirectorFactoryConfig
                    .withEasyScoreCalculatorClass(NQueensMapBasedEasyScoreCalculator.class);
            case JAVA_INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(NQueensAdvancedIncrementalScoreCalculator.class);
        };
    }

    @Override
    protected SolutionDescriptor<NQueens> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(NQueens.class, Queen.class);
    }

    @Override
    protected NQueens readOriginalSolution() {
        final SolutionFileIO<NQueens> solutionFileIO = new NQueensSolutionFileIO();
        return solutionFileIO.read(new File("data/nqueens-256.json"));
    }

}
