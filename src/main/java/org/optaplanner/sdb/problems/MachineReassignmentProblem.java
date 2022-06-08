package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.optional.score.MachineReassignmentIncrementalScoreCalculator;
import org.optaplanner.examples.machinereassignment.score.MachineReassignmentConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class MachineReassignmentProblem
        extends AbstractProblem<MachineReassignment> {

    public MachineReassignmentProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.MACHINE_REASSIGNMENT, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(MachineReassignmentConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(MachineReassignmentConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/machinereassignment/optional/score/machineReassignmentConstraints.drl");
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(MachineReassignmentIncrementalScoreCalculator.class);
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<MachineReassignment> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(MachineReassignment.class, MrProcessAssignment.class);
    }

    @Override
    protected MachineReassignment readOriginalSolution() {
        final XStreamSolutionFileIO<MachineReassignment> solutionFileIO =
                new XStreamSolutionFileIO<>(MachineReassignment.class);
        return solutionFileIO.read(new File("data/machinereassignment-a23.xml"));
    }

}
