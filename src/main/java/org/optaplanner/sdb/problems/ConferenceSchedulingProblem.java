package org.optaplanner.sdb.problems;

import java.io.File;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import org.optaplanner.examples.conferencescheduling.score.ConferenceSchedulingConstraintProvider;
import org.optaplanner.sdb.Example;
import org.optaplanner.sdb.ScoreDirectorType;

public final class ConferenceSchedulingProblem extends AbstractProblem<ConferenceSolution> {

    public ConferenceSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.CONFERENCE_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        return switch (scoreDirectorType) {
            case CONSTRAINT_STREAMS_BAVET -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(ConferenceSchedulingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS -> scoreDirectorFactoryConfig
                    .withConstraintProviderClass(ConferenceSchedulingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL -> scoreDirectorFactoryConfig
                    .withScoreDrls("org/optaplanner/examples/conferencescheduling/optional/score/conferenceSchedulingConstraints.drl");
            default -> throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
        };
    }

    @Override
    protected SolutionDescriptor<ConferenceSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(ConferenceSolution.class, Talk.class);
    }

    @Override
    protected ConferenceSolution readOriginalSolution() {
        return new ConferenceSchedulingXlsxFileIO()
                .read(new File("data/conferencescheduling-216-18-20.xlsx"));
    }

}
