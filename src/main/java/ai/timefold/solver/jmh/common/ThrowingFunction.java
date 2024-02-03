package ai.timefold.solver.jmh.common;

import java.io.IOException;

public interface ThrowingFunction<A, B> {

    B apply(A a) throws IOException;

}
