package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.params.ScoreDirector;
import org.optaplanner.examples.app.params.Example;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.optional.score.InvestmentConstraintProvider;
import org.optaplanner.examples.investment.optional.score.InvestmentEasyScoreCalculator;
import org.optaplanner.examples.investment.optional.score.InvestmentIncrementalScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public final class InvestmentProblem extends AbstractProblem<InvestmentSolution, AssetClassAllocation, Long> {

    public InvestmentProblem(ScoreDirector scoreDirector) {
        super(Example.INVESTMENT, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(InvestmentConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/investment/solver/investmentConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(InvestmentEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(InvestmentIncrementalScoreCalculator.class);
            case CONSTRAINT_STREAMS_BAVET:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<InvestmentSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(InvestmentSolution.class, AssetClassAllocation.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "quantityMillis";
    }

    @Override
    protected InvestmentSolution readOriginalSolution() {
        final XStreamSolutionFileIO<InvestmentSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(InvestmentSolution.class);
        return solutionFileIO.read(new File("data/investment-de_smet_1.xml"));
    }

    @Override
    protected Class<AssetClassAllocation> getEntityClass() {
        return AssetClassAllocation.class;
    }

}
