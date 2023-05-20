package ai.timefold.solver.sdb.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.conferencescheduling.domain.ConferenceSolution;
import ai.timefold.solver.examples.conferencescheduling.domain.Talk;
import ai.timefold.solver.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import ai.timefold.solver.examples.conferencescheduling.score.ConferenceSchedulingConstraintProvider;
import ai.timefold.solver.sdb.Example;
import ai.timefold.solver.sdb.ScoreDirectorType;

public final class ConferenceSchedulingProblem extends AbstractProblem<ConferenceSolution> {

    public ConferenceSchedulingProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.CONFERENCE_SCHEDULING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        if (Objects.requireNonNull(scoreDirectorType) == ScoreDirectorType.CONSTRAINT_STREAMS_BAVET) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(ConferenceSchedulingConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
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
