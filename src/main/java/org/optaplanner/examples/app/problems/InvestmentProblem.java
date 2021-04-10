package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.investment.domain.AssetClassAllocation;
import org.optaplanner.examples.investment.domain.InvestmentSolution;
import org.optaplanner.examples.investment.optional.score.InvestmentConstraintProvider;
import org.optaplanner.examples.investment.optional.score.InvestmentEasyScoreCalculator;
import org.optaplanner.examples.investment.optional.score.InvestmentIncrementalScoreCalculator;

import java.util.List;

public final class InvestmentProblem extends AbstractProblem<InvestmentSolution, AssetClassAllocation, Long> {

    public InvestmentProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
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
                        .withScoreDrls("/org/optaplanner/examples/investment/solver/investmentConstraints.drl");
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
        return new SolutionDescriptor<>(InvestmentSolution.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "quantityMillis";
    }

    @Override
    protected InvestmentSolution readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<AssetClassAllocation> getEntities(InvestmentSolution investmentSolution) {
        return investmentSolution.getAssetClassAllocationList();
    }

    @Override
    protected Long readValue(AssetClassAllocation assetClassAllocation) {
        return assetClassAllocation.getQuantityMillis();
    }

    @Override
    protected void writeValue(AssetClassAllocation assetClassAllocation, Long aLong) {
        assetClassAllocation.setQuantityMillis(aLong);
    }

}
