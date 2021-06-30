package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingConstraintProvider;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingIncrementalScoreCalculator;
import org.optaplanner.examples.cloudbalancing.optional.score.CloudBalancingMapBasedEasyScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class CloudBalancingProblem extends AbstractProblem<CloudBalance, CloudProcess> {

    public CloudBalancingProblem(ScoreDirector scoreDirector) {
        super(Example.CLOUD_BALANCING, scoreDirector);
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
                        .withScoreDrls("org/optaplanner/examples/cloudbalancing/solver/cloudBalancingConstraints.drl");
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
        return SolutionDescriptor.buildSolutionDescriptor(CloudBalance.class, CloudProcess.class);
    }

    @Override
    protected List<String> getEntityVariableNames() {
        return Collections.singletonList("computer");
    }

    @Override
    protected CloudBalance readOriginalSolution() {
        final XStreamSolutionFileIO<CloudBalance> solutionFileIO = new XStreamSolutionFileIO<>(CloudBalance.class);
        return solutionFileIO.read(new File("data/cloudbalancing-1600-4800.xml"));
    }

    @Override
    protected Class<CloudProcess> getEntityClass() {
        return CloudProcess.class;
    }

}
