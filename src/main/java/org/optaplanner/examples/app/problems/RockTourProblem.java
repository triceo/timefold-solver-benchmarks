package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockStandstill;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;
import org.optaplanner.examples.rocktour.optional.score.RockTourConstraintProvider;

import java.util.List;

public final class RockTourProblem extends AbstractProblem<RockTourSolution, RockShow, RockStandstill> {

    public RockTourProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(RockTourConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(RockTourConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/rocktour/solver/rockTourConstraints.drl");
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<RockTourSolution> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(RockTourSolution.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "previousStandstill";
    }

    @Override
    protected RockTourSolution readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<RockShow> getEntities(RockTourSolution rockTourSolution) {
        return rockTourSolution.getShowList();
    }

    @Override
    protected RockStandstill readValue(RockShow rockShow) {
        return rockShow.getPreviousStandstill();
    }

    @Override
    protected void writeValue(RockShow rockShow, RockStandstill rockStandstill) {
        rockShow.setPreviousStandstill(rockStandstill);
    }

}
