package org.wildfly.metrics.scheduler;

import org.jboss.dmr.ModelNode;
import org.wildfly.metrics.scheduler.cfg.Address;
import org.wildfly.metrics.scheduler.cfg.Configuration;
import org.wildfly.metrics.scheduler.cfg.ResourceRef;
import org.wildfly.metrics.scheduler.impl.IntervalBasedScheduler;

import java.util.ArrayList;
import java.util.List;

/**
 * The core service that creates task lists from a configuration
 * and schedules work through a {@link Scheduler}.
 * The resulting data will be pushed to the {@link org.wildfly.metrics.scheduler.StorageAdapter}
 *
 * @author Heiko Braun
 * @since 10/10/14
 */
public class Service implements TopologyChangeListener{

    private final Configuration configuration;
    private final Scheduler scheduler;
    private final TaskCompletionHandler<ModelNode> completionHandler;

    public Service(Configuration configuration) {

        this.configuration = configuration;
        this.completionHandler = new DebugCompletionHandler();
        this.scheduler = new IntervalBasedScheduler(
                2, // threads
                configuration.getHost(),
                configuration.getPort(),
                completionHandler
        );
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

    class DebugCompletionHandler implements TaskCompletionHandler<ModelNode> {
        @Override
        public void onCompleted(Task t, ModelNode data) {
            System.out.println(t + " > "+ data);
        }

        @Override
        public void onFailed(Task t, Throwable e) {
            System.out.println("Task failed: "+e.getMessage());
        }
    }
}
