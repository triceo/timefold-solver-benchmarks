package org.optaplanner.sdb.problems;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockStandstill;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;
import org.optaplanner.examples.rocktour.optional.score.RockTourConstraintProvider;
import org.optaplanner.examples.rocktour.persistence.RockTourXlsxFileIO;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirector;

public final class RockTourProblem extends AbstractProblem<RockTourSolution, RockShow> {

    public RockTourProblem(ScoreDirector scoreDirector) {
        super(Example.ROCK_TOUR, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_BAVET:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(RockTourConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.BAVET);
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(RockTourConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/rocktour/solver/rockTourConstraints.drl");
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<RockTourSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(RockTourSolution.class, RockShow.class, RockStandstill.class);
    }

    @Override
    protected List<String> getEntityVariableNames() {
        return Collections.singletonList("previousStandstill");
    }

    @Override
    protected RockTourSolution readOriginalSolution() {
        return new RockTourXlsxFileIO()
                .read(new File("data/rocktour-47shows.xlsx"));
    }

    @Override
    protected Class<RockShow> getEntityClass() {
        return RockShow.class;
    }

}
