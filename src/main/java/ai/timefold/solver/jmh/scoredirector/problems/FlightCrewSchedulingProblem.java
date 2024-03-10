package ai.timefold.solver.jmh.scoredirector.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.flightcrewscheduling.domain.Employee;
import ai.timefold.solver.examples.flightcrewscheduling.domain.FlightAssignment;
import ai.timefold.solver.examples.flightcrewscheduling.domain.FlightCrewSolution;
import ai.timefold.solver.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;
import ai.timefold.solver.examples.flightcrewscheduling.score.FlightCrewSchedulingConstraintProvider;
import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;

public final class FlightCrewSchedulingProblem extends AbstractProblem<FlightCrewSolution> {

    public FlightCrewSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.FLIGHT_CREW_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        var scoreDirectorFactoryConfig = buildInitialScoreDirectorFactoryConfig();
        var nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(FlightCrewSchedulingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
    }

    @Override
    protected SolutionDescriptor<FlightCrewSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(FlightCrewSolution.class, FlightAssignment.class,
                Employee.class);
    }

    @Override
    protected FlightCrewSolution readOriginalSolution() {
        return new FlightCrewSchedulingXlsxFileIO()
                .read(new File("data/flightcrewscheduling-875-7-Europe.xlsx"));
    }

}
