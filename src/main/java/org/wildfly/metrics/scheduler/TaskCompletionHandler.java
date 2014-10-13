package org.wildfly.metrics.scheduler;

import org.wildfly.metrics.scheduler.impl.Task;
import org.wildfly.metrics.scheduler.impl.TaskGroup;

/**
 * @author Heiko Braun
 * @since 10/10/14
 */
public interface TaskCompletionHandler<D> {
    void onCompleted(Task t, D data);
    void onFailed(TaskGroup g, Throwable e);
}
