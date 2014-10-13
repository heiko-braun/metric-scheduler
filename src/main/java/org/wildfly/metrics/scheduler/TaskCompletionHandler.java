package org.wildfly.metrics.scheduler;

import org.wildfly.metrics.scheduler.impl.Task;

/**
 * @author Heiko Braun
 * @since 10/10/14
 */
public interface TaskCompletionHandler<D> {
    void onCompleted(Task t, D data);
    void onFailed(Task t, Throwable e);
}
