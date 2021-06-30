package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.optional.score.NurseRosteringConstraintProvider;
import org.optaplanner.examples.nurserostering.persistence.NurseRosterXmlSolutionFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class NurseRosteringProblem extends AbstractProblem<NurseRoster, ShiftAssignment> {

    public NurseRosteringProblem(ScoreDirector scoreDirector) {
        super(Example.NURSE_ROSTERING, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(NurseRosteringConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/nurserostering/solver/nurseRosteringConstraints.drl");
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<NurseRoster> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(NurseRoster.class, ShiftAssignment.class);
    }

    @Override
    protected List<String> getEntityVariableNames() {
        return Collections.singletonList("employee");
    }

    @Override
    protected NurseRoster readOriginalSolution() {
        return new NurseRosterXmlSolutionFileIO()
                .read(new File("data/nurserostering-medium_late01.xml"));
    }

    @Override
    protected Class<ShiftAssignment> getEntityClass() {
        return ShiftAssignment.class;
    }

}
