package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.persistence.NurseRosterSolutionFileIO;
import org.optaplanner.examples.nurserostering.score.NurseRosteringConstraintProvider;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class NurseRosteringProblem extends AbstractProblem<NurseRoster> {

    public NurseRosteringProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.NURSE_ROSTERING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(NurseRosteringConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(NurseRosteringConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
    }

    @Override
    protected SolutionDescriptor<NurseRoster> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(NurseRoster.class, ShiftAssignment.class);
    }

    @Override
    protected NurseRoster readOriginalSolution() {
        return new NurseRosterSolutionFileIO()
                .read(new File("data/nurserostering-medium_late01.json"));
    }

}
