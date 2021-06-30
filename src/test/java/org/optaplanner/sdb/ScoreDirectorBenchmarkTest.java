package org.optaplanner.sdb;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.sdb.params.Example;
import org.optaplanner.sdb.params.ScoreDirectorType;
import org.optaplanner.sdb.problems.Problem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ScoreDirectorBenchmarkTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScoreDirectorBenchmarkTest.class);

    @ParameterizedTest
    @MethodSource("scoreDirectorTypeAndExampleProvider")
    void runTest(ScoreDirectorType scoreDirectorType, Example example) {
        LOGGER.info("Testing {} for {}.", scoreDirectorType, example);
        Assertions.assertDoesNotThrow(() -> {
            final Problem problem = example.create(scoreDirectorType);
            problem.setupTrial();
            problem.setupIteration();
            problem.setupInvocation();
            problem.runInvocation();
            problem.tearDownInvocation();
            problem.tearDownIteration();
            problem.teardownTrial();
        });
    }

    public static Stream<Arguments> scoreDirectorTypeAndExampleProvider() {
        return Stream.of(ScoreDirectorType.values())
                .flatMap(scoreDirectorType -> Stream.of(Example.values())
                        .filter(example -> example.isSupportedOn(scoreDirectorType))
                        .map(example -> Arguments.arguments(scoreDirectorType, example)));
    }

}
