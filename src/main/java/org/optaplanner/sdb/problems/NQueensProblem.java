package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.optional.score.NQueensAdvancedIncrementalScoreCalculator;
import org.optaplanner.examples.nqueens.optional.score.NQueensMapBasedEasyScoreCalculator;
import org.optaplanner.examples.nqueens.persistence.NQueensSolutionFileIO;
import org.optaplanner.examples.nqueens.score.NQueensConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

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
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(NQueensConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL -> scoreDirectorFactoryConfig
                    .withScoreDrls("org/optaplanner/examples/nqueens/optional/score/nQueensConstraints.drl");
            case JAVA_EASY -> scoreDirectorFactoryConfig
                    .withEasyScoreCalculatorClass(NQueensMapBasedEasyScoreCalculator.class);
            case JAVA_INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(NQueensAdvancedIncrementalScoreCalculator.class);
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
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
