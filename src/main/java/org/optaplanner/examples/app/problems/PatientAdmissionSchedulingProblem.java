package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.pas.domain.Bed;
import org.optaplanner.examples.pas.domain.BedDesignation;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;
import org.optaplanner.examples.pas.optional.score.PatientAdmissionScheduleConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;
import java.util.List;

public final class PatientAdmissionSchedulingProblem
        extends AbstractProblem<PatientAdmissionSchedule, BedDesignation, Bed> {

    public PatientAdmissionSchedulingProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(PatientAdmissionScheduleConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/pas/solver/patientAdmissionScheduleConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<PatientAdmissionSchedule> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(PatientAdmissionSchedule.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "bed";
    }

    @Override
    protected PatientAdmissionSchedule readAndInitializeSolution() {
        final XStreamSolutionFileIO<PatientAdmissionSchedule> solutionFileIO =
                new XStreamSolutionFileIO<>(PatientAdmissionSchedule.class);
        return solutionFileIO.read(new File("data/pas-12.xml"));
    }

    @Override
    protected List<BedDesignation> getEntities(PatientAdmissionSchedule patientAdmissionSchedule) {
        return patientAdmissionSchedule.getBedDesignationList();
    }

    @Override
    protected Bed readValue(BedDesignation bedDesignation) {
        return bedDesignation.getBed();
    }

    @Override
    protected void writeValue(BedDesignation bedDesignation, Bed bed) {
        bedDesignation.setBed(bed);
    }

}
