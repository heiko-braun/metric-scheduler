package org.wildfly.metrics.scheduler.storage;

import org.wildfly.metrics.scheduler.polling.Task;

/**
 * @author Heiko Braun
 * @since 10/10/14
 */
public interface StorageAdapter {
    void store(Task task, String value);
}
