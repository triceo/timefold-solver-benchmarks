package org.optaplanner.examples.app.problems;

import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.app.params.ScoreDirector;
import org.optaplanner.examples.app.params.Example;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.optional.score.TspConstraintProvider;
import org.optaplanner.examples.tsp.optional.score.TspEasyScoreCalculator;
import org.optaplanner.examples.tsp.optional.score.TspIncrementalScoreCalculator;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

import java.io.File;

public final class TspProblem extends AbstractProblem<TspSolution, Visit, Standstill> {

    public TspProblem(ScoreDirector scoreDirector) {
        super(Example.TSP, scoreDirector);
    }

    @Override
    protected ScoreDirectorFactoryConfig buildScoreDirectorFactoryConfig(ScoreDirector scoreDirector) {
        ScoreDirectorFactoryConfig scoreDirectorFactoryConfig = new ScoreDirectorFactoryConfig();
        switch (scoreDirector) {
            case CONSTRAINT_STREAMS_DROOLS:
                return scoreDirectorFactoryConfig
                        .withConstraintProviderClass(TspConstraintProvider.class)
                        .withConstraintStreamImplType(ConstraintStreamImplType.DROOLS);
            case DRL:
                return scoreDirectorFactoryConfig
                        .withScoreDrls("org/optaplanner/examples/tsp/solver/tspConstraints.drl");
            case JAVA_EASY:
                return scoreDirectorFactoryConfig
                        .withEasyScoreCalculatorClass(TspEasyScoreCalculator.class);
            case JAVA_INCREMENTAL:
                return scoreDirectorFactoryConfig
                        .withIncrementalScoreCalculatorClass(TspIncrementalScoreCalculator.class);
            case CONSTRAINT_STREAMS_BAVET:
            default:
                throw new UnsupportedOperationException("Score director: " + scoreDirector);
        }
    }

    @Override
    protected SolutionDescriptor<TspSolution> buildSolutionDescriptor() {
        return SolutionDescriptor.buildSolutionDescriptor(TspSolution.class, Visit.class);
    }

    @Override
    protected String getEntityVariableName() {
        return "previousStandstill";
    }

    @Override
    protected TspSolution readOriginalSolution() {
        final XStreamSolutionFileIO<TspSolution> solutionFileIO =
                new XStreamSolutionFileIO<>(TspSolution.class);
        return solutionFileIO.read(new File("data/tsp-lu980.xml"));
    }

    @Override
    protected Class<Visit> getEntityClass() {
        return Visit.class;
    }

}
