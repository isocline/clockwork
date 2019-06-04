package isocline.clockwork.flow.func;

import isocline.clockwork.WorkEvent;


/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code Consumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(Object)}.
 *
 * @param <T> the type of the input to the operation
 *
 * @since 1.8
 */
@FunctionalInterface
public interface WorkEventConsumer {

    /**
     * Performs this operation on the given argument.
     *
     * @param e the input argument
     */
    void accept(WorkEvent e) throws Throwable;

}
