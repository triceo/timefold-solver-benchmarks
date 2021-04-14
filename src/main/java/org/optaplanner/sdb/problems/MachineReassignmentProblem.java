package org.optaplanner.sdb.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.sdb.params.ScoreDirector;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.optional.score.MachineReassignmentConstraintProvider;
import org.optaplanner.examples.machinereassignment.score.MachineReassignmentIncrementalScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public final class MachineReassignmentProblem
        extends AbstractProblem<MachineReassignment, MrProcessAssignment> {

    public MachineReassignmentProblem(ScoreDirector scoreDirector) {
        super(Example.MACHINE_REASSIGNMENT, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(MachineReassignmentConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/machinereassignment/solver/machineReassignmentConstraints.drl");
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(MachineReassignmentIncrementalScoreCalculator.class);
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<MachineReassignment> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(MachineReassignment.class, MrProcessAssignment.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "machine";
    }

    @Override
    protected MachineReassignment readOriginalSolution() {
        final XStreamSolutionFileIO<MachineReassignment> solutionFileIO =
                new XStreamSolutionFileIO<>(MachineReassignment.class);
        return solutionFileIO.read(new File("data/machinereassignment-a23.xml"));
    }

    @Override
    protected Class<MrProcessAssignment> getEntityClass() {
        return MrProcessAssignment.class;
    }

}
