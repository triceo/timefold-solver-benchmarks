package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.optional.score.TspConstraintProvider;
import org.optaplanner.examples.tsp.optional.score.TspEasyScoreCalculator;
import org.optaplanner.examples.tsp.optional.score.TspIncrementalScoreCalculator;

import java.util.List;

public final class TspProblem extends AbstractProblem<TspSolution, Visit, Standstill> {

    public TspProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TspConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/tsp/solver/tspConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(TspEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(TspIncrementalScoreCalculator.class);
            case CONSTRAINT_STREAMS_BAVET:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<TspSolution> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(TspSolution.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "previousStandstill";
    }

    @Override
    protected TspSolution readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<Visit> getEntities(TspSolution tspSolution) {
        return tspSolution.getVisitList();
    }

    @Override
    protected Standstill readValue(Visit visit) {
        return visit.getPreviousStandstill();
    }

    @Override
    protected void writeValue(Visit visit, Standstill standstill) {
        visit.setPreviousStandstill(standstill);
    }

}
