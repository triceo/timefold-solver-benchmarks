package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.Room;
import org.optaplanner.examples.examination.optional.score.ExaminationConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;
import java.util.List;

public final class ExaminationProblem extends AbstractProblem<Examination, Exam, Room> {

    public ExaminationProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(ExaminationConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("/org/optaplanner/examples/examination/solver/examinationConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<Examination> buildSolutionDescriptor() {
        return new SolutionDescriptor<>(Examination.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "room";
    }

    @Override
    protected Examination readAndInitializeSolution() {
        final XStreamSolutionFileIO<Examination> solutionFileIO =
                new XStreamSolutionFileIO<>(Examination.class);
        return solutionFileIO.read(new File("data/examination-comp_set8.xml"));
    }

    @Override
    protected List<Exam> getEntities(Examination examination) {
        return examination.getExamList();
    }

    @Override
    protected Room readValue(Exam exam) {
        return exam.getRoom();
    }

    @Override
    protected void writeValue(Exam exam, Room room) {
        exam.setRoom(room);
    }

}
