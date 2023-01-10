package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.persistence.PatientAdmissionScheduleSolutionFileIO;
import org.optaplanner.examples.pas.score.PatientAdmissionScheduleConstraintProvider;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class PatientAdmissionSchedulingProblem
        extends AbstractProblem<PatientAdmissionSchedule> {

    public PatientAdmissionSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.PATIENT_ADMISSION_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(PatientAdmissionScheduleConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(PatientAdmissionScheduleConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
    }

    @Override
    protected SolutionDescriptor<PatientAdmissionSchedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(PatientAdmissionSchedule.class, BedDesignation.class);
    }

    @Override
    protected PatientAdmissionSchedule readOriginalSolution() {
        final SolutionFileIO<PatientAdmissionSchedule> solutionFileIO = new PatientAdmissionScheduleSolutionFileIO();
        return solutionFileIO.read(new File("data/pas-12.json"));
    }

}
