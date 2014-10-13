package org.wildfly.metrics.scheduler;

import org.wildfly.metrics.scheduler.polling.Task;
import org.wildfly.metrics.scheduler.polling.TaskGroup;

/**
 * @author Heiko Braun
 * @since 10/10/14
 */
public interface TaskCompletionHandler<D> {
    void start();
    void shutdown();
    void onCompleted(Task t, D data);
    void onFailed(TaskGroup g, Throwable e);
}
