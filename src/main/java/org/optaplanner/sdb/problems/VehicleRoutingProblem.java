package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.vehiclerouting.domain.Customer;
import org.optaplanner.examples.vehiclerouting.domain.Vehicle;
import org.optaplanner.examples.vehiclerouting.domain.VehicleRoutingSolution;
import org.optaplanner.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingEasyScoreCalculator;
import org.optaplanner.examples.vehiclerouting.optional.score.VehicleRoutingIncrementalScoreCalculator;
import org.optaplanner.examples.vehiclerouting.persistence.VehicleRoutingSolutionFileIO;
import org.optaplanner.examples.vehiclerouting.score.VehicleRoutingConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class VehicleRoutingProblem extends AbstractProblem<VehicleRoutingSolution> {

    public VehicleRoutingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.VEHICLE_ROUTING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(VehicleRoutingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(VehicleRoutingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case JAVA_EASY -> scoreDirectorFactoryConfig
                    .withEasyScoreCalculatorClass(VehicleRoutingEasyScoreCalculator.class);
            case JAVA_INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(VehicleRoutingIncrementalScoreCalculator.class);
        };
    }

    @Override
    protected SolutionDescriptor<VehicleRoutingSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(VehicleRoutingSolution.class, Vehicle.class, Customer.class,
                TimeWindowedCustomer.class);
    }

    @Override
    protected VehicleRoutingSolution readOriginalSolution() {
        final SolutionFileIO<VehicleRoutingSolution> solutionFileIO = new VehicleRoutingSolutionFileIO();
        return solutionFileIO.read(new File("data/vehiclerouting-belgium-tw-n2750-k55.json"));
    }

}
