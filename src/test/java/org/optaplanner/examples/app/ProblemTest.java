package org.optaplanner.examples.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.examples.app.directors.ScoreDirector;
import org.optaplanner.examples.app.params.Example;
import org.optaplanner.examples.app.problems.Problem;

import java.util.stream.Stream;

final class ProblemTest {

    @ParameterizedTest
    @MethodSource("scoreDirectorAndExampleProvider")
    void runTest(ScoreDirector scoreDirector, Example example) {
        Assertions.assertDoesNotThrow(() -> {
            final Problem problem = example.create(scoreDirector);
            problem.setupTrial();
            problem.setupIteration();
            problem.setupInvocation();
            problem.runInvocation();
            problem.tearDownInvocation();
            problem.tearDownIteration();
            problem.teardownTrial();
        });
    }

    public static Stream<Arguments> scoreDirectorAndExampleProvider() {
        return Stream.of(ScoreDirector.values())
                .flatMap(scoreDirector -> Stream.of(Example.values())
                        .filter(example -> example.isSupportedOn(scoreDirector))
                        .map(example -> Arguments.arguments(scoreDirector, example)));
    }

}
