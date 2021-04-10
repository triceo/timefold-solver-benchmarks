package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingConstraintProvider;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingIncrementalScoreCalculator;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingMapBasedEasyScoreCalculator;

import java.util.List;

public final class CloudBalancingProblem extends AbstractProblem<CloudBalance, CloudProcess, CloudComputer> {

    public CloudBalancingProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(CloudBalancingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/cloudBalancing/solver/cloudBalancingConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(CloudBalancingMapBasedEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(CloudBalancingIncrementalScoreCalculator.class);
            case CONSTRAINT_STREAMS_BAVET:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<CloudBalance> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(CloudBalance.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "computer";
    }

    @Override
    protected CloudBalance readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<CloudProcess> getEntities(CloudBalance cloudBalance) {
        return cloudBalance.getProcessList();
    }

    @Override
    protected CloudComputer readValue(CloudProcess cloudProcess) {
        return cloudProcess.getComputer();
    }

    @Override
    protected void writeValue(CloudProcess cloudProcess, CloudComputer cloudComputer) {
        cloudProcess.setComputer(cloudComputer);
    }

}
