package ai.timefold.solver.sdb.problems;

import java.io.File;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.cloudbalancing.domain.CloudBalance;
import ai.timefold.solver.examples.cloudbalancing.domain.CloudProcess;
import ai.timefold.solver.examples.cloudbalancing.optional.score.CloudBalancingIncrementalScoreCalculator;
import ai.timefold.solver.examples.cloudbalancing.optional.score.CloudBalancingMapBasedEasyScoreCalculator;
import ai.timefold.solver.examples.cloudbalancing.persistence.CloudBalanceSolutionFileIO;
import ai.timefold.solver.examples.cloudbalancing.score.CloudBalancingConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class CloudBalancingProblem extends AbstractProblem<CloudBalance> {

    public CloudBalancingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.CLOUD_BALANCING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(CloudBalancingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case EASY -> scoreDirectorFactoryConfig
                    .withEasyScoreCalculatorClass(CloudBalancingMapBasedEasyScoreCalculator.class);
            case INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(CloudBalancingIncrementalScoreCalculator.class);
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
