package ai.timefold.solver.jmh.scoredirector.problems;

import java.io.File;
import java.util.Objects;

import ai.timefold.solver.core.api.score.stream.ConstraintStreamImplType;
import ai.timefold.solver.core.config.score.director.ScoreDirectorFactoryConfig;
import ai.timefold.solver.core.impl.domain.solution.descriptor.SolutionDescriptor;
import ai.timefold.solver.examples.nurserostering.domain.NurseRoster;
import ai.timefold.solver.examples.nurserostering.domain.ShiftAssignment;
import ai.timefold.solver.examples.nurserostering.persistence.NurseRosterSolutionFileIO;
import ai.timefold.solver.examples.nurserostering.score.NurseRosteringConstraintProvider;
import ai.timefold.solver.jmh.scoredirector.Example;
import ai.timefold.solver.jmh.scoredirector.ScoreDirectorType;

public final class NurseRosteringProblem extends AbstractProblem<NurseRoster> {

    public NurseRosteringProblem(ScoreDirectorType scoreDirectorType) {
        super(Example.NURSE_ROSTERING, scoreDirectorType);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirectorType scoreDirectorType) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        ScoreDirectorType nonNullScoreDirectorType = Objects.requireNonNull(scoreDirectorType);
        if (nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS
                || nonNullScoreDirectorType == ScoreDirectorType.CONSTRAINT_STREAMS_JUSTIFIED) {
            return scoreDirectorFactoryConfig
                    .withConstraintProviderClass(NurseRosteringConstraintProvider.class)
                    .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
        }
        throw new UnsupportedOperationException("Score director: " + scoreDirectorType);
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
