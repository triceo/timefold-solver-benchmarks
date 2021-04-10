package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingConstraintProvider;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingEasyScoreCalculator;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingIncrementalScoreCalculator;

import java.util.List;

public final class VehicleRoutingProblem extends AbstractProblem<VehicleRoutingSolution, Customer, Standstill> {

    public VehicleRoutingProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(VehicleRoutingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(VehicleRoutingConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/vehiclerouting/solver/vehicleRoutingConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(VehicleRoutingEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(VehicleRoutingIncrementalScoreCalculator.class);
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<VehicleRoutingSolution> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(VehicleRoutingSolution.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "previousStandstill";
    }

    @Override
    protected VehicleRoutingSolution readAndInitializeSolution() {
        return null;
    }

    @Override
    protected List<Customer> getEntities(VehicleRoutingSolution vehicleRoutingSolution) {
        return vehicleRoutingSolution.getCustomerList();
    }

    @Override
    protected Standstill readValue(Customer customer) {
        return customer.getPreviousStandstill();
    }

    @Override
    protected void writeValue(Customer customer, Standstill standstill) {
        customer.setPreviousStandstill(standstill);
    }

}
