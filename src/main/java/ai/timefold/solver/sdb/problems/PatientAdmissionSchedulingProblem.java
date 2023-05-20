package ai.timefold.solver.sdb.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.pas.domain.BedDesignation;
import ai.timefold.solver.examples.pas.domain.PatientAdmissionSchedule;
import ai.timefold.solver.examples.pas.persistence.PatientAdmissionScheduleSolutionFileIO;
import ai.timefold.solver.examples.pas.score.PatientAdmissionScheduleConstraintProvider;
import ai.timefold.solver.persistence.common.api.domain.solution.SolutionFileIO;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class PatientAdmissionSchedulingProblem
        extends AbstractProblem<PatientAdmissionSchedule> {

    public PatientAdmissionSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.PATIENT_ADMISSION_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        if (Objects.requireNonNull(scoreDirectorType) == ScoreDirectorType.CONSTRAINT_STREAMS_BAVET) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(PatientAdmissionScheduleConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
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
