package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Coach;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;
import org.optaplanner.examples.coachshuttlegathering.optional.score.CoachShuttleGatheringEasyScoreCalculator;
import org.optaplanner.examples.coachshuttlegathering.score.CoachShuttleGatheringConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class CoachShuttleGatheringProblem extends AbstractProblem<CoachShuttleGatheringSolution> {

    public CoachShuttleGatheringProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.COACH_SHUTTLE_GATHERING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(CoachShuttleGatheringConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(CoachShuttleGatheringConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/coachshuttlegathering/optional/score/coachShuttleGatheringConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(CoachShuttleGatheringEasyScoreCalculator.class);
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<CoachShuttleGatheringSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(CoachShuttleGatheringSolution.class, Coach.class,
                Shuttle.class, BusStop.class, StopOrHub.class, BusOrStop.class);
    }

    @Override
    protected CoachShuttleGatheringSolution readOriginalSolution() {
        final XStreamSolutionFileIO<CoachShuttleGatheringSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(CoachShuttleGatheringSolution.class);
        return solutionFileIO.read(new File("data/coachshuttlegathering-demo1.xml"));
    }

}
