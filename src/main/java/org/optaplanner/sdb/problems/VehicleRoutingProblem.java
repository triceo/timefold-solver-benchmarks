package org.optaplanner.sdb.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.sdb.params.ScoreDirector;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Standstill;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingConstraintProvider;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingEasyScoreCalculator;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingIncrementalScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public final class VehicleRoutingProblem extends AbstractProblem<VehicleRoutingSolution, Customer> {

    public VehicleRoutingProblem(ScoreDirector scoreDirector) {
        super(Example.VEHICLE_ROUTING, scoreDirector);
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
                        .withScoreDrls("org/optaplanner/examples/vehiclerouting/solver/vehicleRoutingConstraints.drl");
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
        return SolutionDescriptor.buildSolutionDescriptor(VehicleRoutingSolution.class, Standstill.class,
                Customer.class, TimeWindowedCustomer.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "previousStandstill";
    }

    @Override
    protected VehicleRoutingSolution readOriginalSolution() {
        final XStreamSolutionFileIO<VehicleRoutingSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(VehicleRoutingSolution.class);
        return solutionFileIO.read(new File("data/vehiclerouting-belgium-tw-n2750-k55.xml"));
    }

    @Override
    protected Class<Customer> getEntityClass() {
        return Customer.class;
    }

}
