package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.optional.score.FlightCrewSchedulingConstraintProvider;

import java.util.List;

public final class FlightCrewSchedulingProblem extends AbstractProblem<FlightCrewSolution, FlightAssignment, Employee> {

    public FlightCrewSchedulingProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
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
                        .withScoreDrls("/org/optaplanner/examples/flightcrewscheduling/solver/flightCrewSchedulingConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<FlightCrewSolution> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(FlightCrewSolution.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "employee";
    }

    @Override
    protected FlightCrewSolution readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<FlightAssignment> getEntities(FlightCrewSolution flightCrewSolution) {
        return flightCrewSolution.getFlightAssignmentList();
    }

    @Override
    protected Employee readValue(FlightAssignment flightAssignment) {
        return flightAssignment.getEmployee();
    }

    @Override
    protected void writeValue(FlightAssignment flightAssignment, Employee employee) {
        flightAssignment.setEmployee(employee);
    }

}
