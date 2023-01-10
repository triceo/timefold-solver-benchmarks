package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingIncrementalScoreCalculator;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingMapBasedEasyScoreCalculator;
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalanceSolutionFileIO;
import org.optaplanner.examples.cloudbalancing.score.CloudBalancingConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class CloudBalancingProblem extends AbstractProblem<CloudBalance> {

    public CloudBalancingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.CLOUD_BALANCING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(CloudBalancingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(CloudBalancingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case JAVA_EASY -> scoreDirectorFactoryConfig
                    .withEasyScoreCalculatorClass(CloudBalancingMapBasedEasyScoreCalculator.class);
            case JAVA_INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(CloudBalancingIncrementalScoreCalculator.class);
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
    }

    @Override
    protected SolutionDescriptor<CloudBalance> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(CloudBalance.class, CloudProcess.class);
    }

    @Override
    protected CloudBalance readOriginalSolution() {
        final SolutionFileIO<CloudBalance> solutionFileIO = new CloudBalanceSolutionFileIO();
        return solutionFileIO.read(new File("data/cloudbalancing-1600-4800.json"));
    }

}
