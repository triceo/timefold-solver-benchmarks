package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.params.ScoreDirector;
import org.optaplanner.examples.app.params.Example;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.optional.score.FlightCrewSchedulingConstraintProvider;
import org.optaplanner.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;

import java.io.File;

public final class FlightCrewSchedulingProblem extends AbstractProblem<FlightCrewSolution, FlightAssignment, Employee> {

    public FlightCrewSchedulingProblem(ScoreDirector scoreDirector) {
        super(Example.FLIGHT_CREW_SCHEDULING, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(FlightCrewSchedulingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/flightcrewscheduling/solver/flightCrewSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<FlightCrewSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(FlightCrewSolution.class, FlightAssignment.class,
                Employee.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "employee";
    }

    @Override
    protected FlightCrewSolution readOriginalSolution() {
        return new FlightCrewSchedulingXlsxFileIO()
                .read(new File("data/flightcrewscheduling-875-7-Europe.xlsx"));
    }

    @Override
    protected Class<FlightAssignment> getEntityClass() {
        return FlightAssignment.class;
    }

}
