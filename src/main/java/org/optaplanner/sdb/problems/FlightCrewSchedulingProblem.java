package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;
import org.optaplanner.examples.flightcrewscheduling.score.FlightCrewSchedulingConstraintProvider;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirectorType;

public final class FlightCrewSchedulingProblem extends AbstractProblem<FlightCrewSolution> {

    public FlightCrewSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.FLIGHT_CREW_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(FlightCrewSchedulingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/flightcrewscheduling/optional/score/flightCrewSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
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
