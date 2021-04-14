package org.optaplanner.sdb.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.sdb.params.ScoreDirector;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.examples.coachshuttlegathering.domain.*;
import org.optaplanner.examples.coachshuttlegathering.optional.score.CoachShuttleGatheringConstraintProvider;
import org.optaplanner.examples.coachshuttlegathering.optional.score.CoachShuttleGatheringEasyScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public final class CoachShuttleGatheringProblem extends AbstractProblem<CoachShuttleGatheringSolution, Shuttle> {

    public CoachShuttleGatheringProblem(ScoreDirector scoreDirector) {
        super(Example.COACH_SHUTTLE_GATHERING, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(CoachShuttleGatheringConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/coachshuttlegathering/solver/coachShuttleGatheringConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(CoachShuttleGatheringEasyScoreCalculator.class);
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<CoachShuttleGatheringSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(CoachShuttleGatheringSolution.class, Coach.class,
                Shuttle.class, BusStop.class, StopOrHub.class, BusOrStop.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "destination";
    }

    @Override
    protected CoachShuttleGatheringSolution readOriginalSolution() {
        final XStreamSolutionFileIO<CoachShuttleGatheringSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(CoachShuttleGatheringSolution.class);
        return solutionFileIO.read(new File("data/coachshuttlegathering-demo1.xml"));
    }

    @Override
    protected Class<Shuttle> getEntityClass() {
        return Shuttle.class;
    }

}
