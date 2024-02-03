package ai.timefold.solver.jmh.coldstart.problems;

import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import org.openjdk.jmh.infra.Blackhole;

public class TimeToSolverFactoryProblem implements Problem {

    private final Class<? extends ConstraintProvider> constraintProviderClass;
    private final Class<?> solutionClass;
    private final Class<?>[] entityClasses;
    
    private SolverConfig solverConfig;

    public TimeToSolverFactoryProblem(Class<? extends ConstraintProvider> constraintProviderClass, Class<?> solutionClass, Class<?>... entityClasses) {
        this.constraintProviderClass = constraintProviderClass;
        this.solutionClass = solutionClass;
        this.entityClasses = entityClasses;
    }

    @Override
    public void setupTrial() {
    }

    @Override
    public void setupIteration() {

    }

    @Override
    public void setupInvocation() {
        solverConfig = new SolverConfig()
                .withSolutionClass(solutionClass)
                .withEntityClasses(entityClasses)
                .withConstraintProviderClass(constraintProviderClass);
    }

    @Override
    public Object runInvocation(Blackhole blackhole) {
        blackhole.consume(solverConfig);
        return SolverFactory.create(solverConfig);
    }

    @Override
    public void tearDownInvocation() {
        solverConfig = null;
    }

    @Override
    public void tearDownIteration() {

    }

    @Override
    public void teardownTrial() {
    }
}
