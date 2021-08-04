package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.score.PatientAdmissionScheduleConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirectorType;

public final class PatientAdmissionSchedulingProblem
        extends AbstractProblem<PatientAdmissionSchedule> {

    public PatientAdmissionSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.PATIENT_ADMISSION_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(PatientAdmissionScheduleConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/pas/optional/score/patientAdmissionScheduleConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        }
    }

    @Override
    protected SolutionDescriptor<PatientAdmissionSchedule> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(PatientAdmissionSchedule.class, BedDesignation.class);
    }

    @Override
    protected PatientAdmissionSchedule readOriginalSolution() {
        final XStreamSolutionFileIO<PatientAdmissionSchedule> solutionFileIO =
                new XStreamSolutionFileIO<>(PatientAdmissionSchedule.class);
        return solutionFileIO.read(new File("data/pas-12.xml"));
    }

}
