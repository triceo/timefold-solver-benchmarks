package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.nqueens.domain.NQueens;
import org.optaplanner.examples.nqueens.domain.Queen;
import org.optaplanner.examples.nqueens.optional.score.NQueensAdvancedIncrementalScoreCalculator;
import org.optaplanner.examples.nqueens.optional.score.NQueensConstraintProvider;
import org.optaplanner.examples.nqueens.optional.score.NQueensMapBasedEasyScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class NQueensProblem extends AbstractProblem<NQueens, Queen> {

    public NQueensProblem(ScoreDirector scoreDirector) {
        super(Example.NQUEENS, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(NQueensConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(NQueensConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/nqueens/solver/nQueensConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(NQueensMapBasedEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(NQueensAdvancedIncrementalScoreCalculator.class);
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<NQueens> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(NQueens.class, Queen.class);
    }

    @Override
    protected List<String> getEntityVariableNames() {
        return Collections.singletonList("row");
    }

    @Override
    protected NQueens readOriginalSolution() {
        final XStreamSolutionFileIO<NQueens> solutionFileIO =
                new XStreamSolutionFileIO<>(NQueens.class);
        return solutionFileIO.read(new File("data/nqueens-256.xml"));
    }

    @Override
    protected Class<Queen> getEntityClass() {
        return Queen.class;
    }

}
