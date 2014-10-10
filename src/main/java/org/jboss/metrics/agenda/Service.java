package org.jboss.metrics.agenda;

import org.jboss.metrics.agenda.address.Address;
import org.jboss.metrics.agenda.impl.IntervalBasedScheduler;
import org.jboss.metrics.agenda.impl.IntervalGrouping;
import org.jboss.metrics.agenda.impl.PrintOperationResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Heiko Braun
 * @since 10/10/14
 */
public class Service implements TopologyChangeListener{

    private final Configuration configuration;
    private final IntervalBasedScheduler scheduler;

    public Service(Configuration configuration) {

        this.configuration = configuration;
        this.scheduler = new IntervalBasedScheduler(1, new PrintOperationResult());
    }

    void start() {

        // turn ResourceRef into Tasks (relative to absolute addresses ...)
        Set<Task> tasks = createTasks(configuration.getResourceRefs());

        scheduler.start(tasks);
    }

    private Set<Task> createTasks(List<ResourceRef> resourceRefs) {
        Set<Task> tasks = new HashSet<>();
        for (ResourceRef ref : resourceRefs) {
            tasks.add(new Task(Address.apply(ref.getAddress()), ref.getAttribute(), ref.getInterval()));
        }
        return tasks;
    }

    void stop() {

        scheduler.stop();
    }

    @Override
    public void onChange() {
        // stop scheduler
        // recalculate tasks
        // restart scheduler
    }
}
