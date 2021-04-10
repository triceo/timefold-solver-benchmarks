package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.tennis.domain.Team;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.optional.score.TennisConstraintProvider;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;
import java.util.List;

public final class TennisProblem extends AbstractProblem<TennisSolution, TeamAssignment, Team> {

    public TennisProblem(ScoreDirector scoreDirector) {
        super(scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TennisConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/tennis/solver/tennisConstraints.drl");
            case CONSTRAINT_STREAMS_BAVET:
            case JAVA_EASY:
            case JAVA_INCREMENTAL:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<TennisSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TennisSolution.class, TeamAssignment.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "team";
    }

    @Override
    protected TennisSolution readAndInitializeSolution() {
        final XStreamSolutionFileIO<TennisSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(TennisSolution.class);
        return solutionFileIO.read(new File("data/tennis-munich-7teams.xml"));
    }

    @Override
    protected List<TeamAssignment> getEntities(TennisSolution tennisSolution) {
        return tennisSolution.getTeamAssignmentList();
    }

}
