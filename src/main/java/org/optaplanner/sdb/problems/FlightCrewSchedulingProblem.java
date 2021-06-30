package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.optional.score.FlightCrewSchedulingConstraintProvider;
import org.optaplanner.examples.flightcrewscheduling.persistence.FlightCrewSchedulingXlsxFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class FlightCrewSchedulingProblem extends AbstractProblem<FlightCrewSolution, FlightAssignment> {

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
    protected List<String> getEntityVariableNames() {
        return Collections.singletonList("employee");
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
