package ai.timefold.solver.jmh.scoredirector.problems;

import java.io.File;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.machinereassignment.domain.MachineReassignment;
import ai.timefold.solver.examples.machinereassignment.domain.MrProcessAssignment;
import ai.timefold.solver.examples.machinereassignment.optional.score.MachineReassignmentIncrementalScoreCalculator;
import ai.timefold.solver.examples.machinereassignment.persistence.MachineReassignmentSolutionFileIO;
import ai.timefold.solver.examples.machinereassignment.score.MachineReassignmentConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;

public final class MachineReassignmentProblem
        extends AbstractProblem<MachineReassignment> {

    public MachineReassignmentProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.MACHINE_REASSIGNMENT, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS, CONSTRAINT_STREAMS_JUSTIFIED -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(MachineReassignmentConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case INCREMENTAL -> scoreDirectorFactoryConfig
                    .withIncrementalScoreCalculatorClass(MachineReassignmentIncrementalScoreCalculator.class);
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
    }

    @Override
    protected SolutionDescriptor<MachineReassignment> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(MachineReassignment.class, MrProcessAssignment.class);
    }

    @Override
    protected MachineReassignment readOriginalSolution() {
        final SolutionFileIO<MachineReassignment> solutionFileIO = new MachineReassignmentSolutionFileIO();
        return solutionFileIO.read(new File("data/machinereassignment-a23.json"));
    }

}
