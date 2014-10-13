package org.wildfly.metrics.scheduler.storage;

import java.util.Set;

/**
 * @author Heiko Braun
 * @since 10/10/14
 */
public interface StorageAdapter {
    void store(Set<Sample> samples);
}
