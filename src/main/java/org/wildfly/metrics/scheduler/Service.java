package org.wildfly.metrics.scheduler;

import org.wildfly.metrics.scheduler.cfg.Address;
import org.wildfly.metrics.scheduler.cfg.Configuration;
import org.wildfly.metrics.scheduler.cfg.ResourceRef;
import org.wildfly.metrics.scheduler.impl.IntervalBasedScheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * The actual agenda service that creates task lists from configuration
 * and schedules work through a {@link Scheduler}
 *
 * @author Heiko Braun
 * @since 10/10/14
 */
public class Service implements TopologyChangeListener {

    private final Configuration configuration;
    private final Scheduler scheduler;

    public Service(Configuration configuration) {

        this.configuration = configuration;
        this.scheduler = new IntervalBasedScheduler(2, configuration.getHost(), configuration.getPort());
    }

    void start() {

        // turn ResourceRef into Tasks (relative to absolute addresses ...)
        List<Task> tasks = createTasks(configuration.getResourceRefs());

        scheduler.schedule(tasks);
    }

    private List<Task> createTasks(List<ResourceRef> resourceRefs) {
        List<Task> tasks = new ArrayList<>();
        for (ResourceRef ref : resourceRefs) {
            tasks.add(new Task(Address.apply(ref.getAddress()), ref.getAttribute(), ref.getInterval()));
        }
        return tasks;
    }

    void stop() {

        scheduler.shutdown();
    }

    @Override
    public void onChange() {
        // shutdown scheduler
        // recalculate tasks
        // restart scheduler
    }
}
