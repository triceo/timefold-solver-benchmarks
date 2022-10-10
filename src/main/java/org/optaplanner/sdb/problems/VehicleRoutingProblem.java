package org.optaplanner.sdb.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingEasyScoreCalculator;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingIncrementalScoreCalculator;
import org.optaplanner.examples.vehiclerouting.score.VehicleRoutingConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

import java.io.File;

public final class VehicleRoutingProblem extends AbstractProblem<VehicleRoutingSolution> {

    public VehicleRoutingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.VEHICLE_ROUTING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
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
                        .withScoreDrls("org/optaplanner/examples/vehiclerouting/optional/score/vehicleRoutingConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(VehicleRoutingEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(VehicleRoutingIncrementalScoreCalculator.class);
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<VehicleRoutingSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(VehicleRoutingSolution.class, Vehicle.class, Customer.class, TimeWindowedCustomer.class);
    }

    @Override
    protected VehicleRoutingSolution readOriginalSolution() {
        final XStreamSolutionFileIO<VehicleRoutingSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(VehicleRoutingSolution.class);
        return solutionFileIO.read(new File("data/vehiclerouting-belgium-tw-n2750-k55.xml"));
    }

}
