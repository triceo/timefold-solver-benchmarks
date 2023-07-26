package ai.timefold.solver.sdb.problems;

import java.io.File;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.vehiclerouting.domain.Customer;
import ai.timefold.solver.examples.vehiclerouting.domain.Vehicle;
import ai.timefold.solver.examples.vehiclerouting.domain.VehicleRoutingSolution;
import ai.timefold.solver.examples.vehiclerouting.domain.timewindowed.TimeWindowedCustomer;
import ai.timefold.solver.examples.vehiclerouting.optional.score.VehicleRoutingEasyScoreCalculator;
import ai.timefold.solver.examples.vehiclerouting.optional.score.VehicleRoutingIncrementalScoreCalculator;
import ai.timefold.solver.examples.vehiclerouting.persistence.VehicleRoutingSolutionFileIO;
import ai.timefold.solver.examples.vehiclerouting.score.VehicleRoutingConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class VehicleRoutingProblem extends AbstractProblem<VehicleRoutingSolution> {

    public VehicleRoutingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.VEHICLE_ROUTING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(VehicleRoutingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case EASY -> scoreDirectorFactoryConfig
                    .withEasyScoreCalculatorClass(VehicleRoutingEasyScoreCalculator.class);
            case INCREMENTAL -> scoreDirectorFactoryConfig
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
