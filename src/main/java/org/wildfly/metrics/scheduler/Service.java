package org.wildfly.metrics.scheduler;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import org.wildfly.metrics.scheduler.config.Address;
import org.wildfly.metrics.scheduler.config.Configuration;
import org.wildfly.metrics.scheduler.config.ResourceRef;
import org.wildfly.metrics.scheduler.polling.IntervalBasedScheduler;
import org.wildfly.metrics.scheduler.polling.Scheduler;
import org.wildfly.metrics.scheduler.polling.Task;
import org.wildfly.metrics.scheduler.report.Monitor;
import org.wildfly.metrics.scheduler.report.StorageReporter;
import org.wildfly.metrics.scheduler.storage.BufferedStorageDispatcher;
import org.wildfly.metrics.scheduler.storage.InfluxStorageAdapter;
import org.wildfly.metrics.scheduler.storage.StorageAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * The core service that creates task lists from a {@link org.wildfly.metrics.scheduler.config.Configuration}
 * and schedules work through a {@link org.wildfly.metrics.scheduler.polling.Scheduler}.
 * The resulting data will be pushed to a {@link org.wildfly.metrics.scheduler.storage.StorageAdapter}
 *
 * @author Heiko Braun
 * @since 10/10/14
 */
public class Service implements TopologyChangeListener {

    private final StorageAdapter storageAdapter;
    private Configuration configuration;
    private Scheduler scheduler;
    private Monitor monitor;
    private ScheduledReporter reporter;
    private boolean started = false;
    private BufferedStorageDispatcher completionHandler;

    /**
     *
     * @param configuration
     */
    public Service(Configuration configuration) {

        this.configuration = configuration;
        this.storageAdapter = new InfluxStorageAdapter(configuration);
        this.completionHandler = new BufferedStorageDispatcher(storageAdapter);  // TODO: make configurable

        final MetricRegistry metrics = new MetricRegistry();
       /* this.reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(MILLISECONDS)
                .build();*/

        this.reporter = StorageReporter.forRegistry(metrics, storageAdapter)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(MILLISECONDS)
                .build();

        this.monitor = createMonitor(metrics);

        this.scheduler = new IntervalBasedScheduler(
                monitor,
                configuration.getSchedulerThreads(),
                configuration.getHost(),
                configuration.getPort()
        );
    }


    private Monitor createMonitor(final MetricRegistry metrics) {
        return new Monitor() {

            private final Timer requestTimer = metrics.timer(name("dmr-request-timer"));
            private final Meter delayCounter = metrics.meter(name("task-delay-rate"));
            private final Meter taskErrorCounter = metrics.meter(name("task-error-rate"));

            @Override
            public Timer getRequestTimer() {
                return requestTimer;
            }

            @Override
            public Meter getDelayedRate() {
                return delayCounter;
            }

            @Override
            public Meter getErrorRate() {
                return taskErrorCounter;
            }
        };
    }

    void start() {

        // turn ResourceRef into Tasks (relative to absolute addresses ...)
        List<Task> tasks = createTasks(configuration.getResourceRefs());
        this.completionHandler.start();
        this.scheduler.schedule(tasks, completionHandler);
    }

    private List<Task> createTasks(List<ResourceRef> resourceRefs) {
        List<Task> tasks = new ArrayList<>();
        for (ResourceRef ref : resourceRefs) {

            // parse sub references (complex attribute support)
            String attribute = ref.getAttribute();
            String subref = null;
            int i = attribute.indexOf("#");
            if(i>0) {
                subref = attribute.substring(i+1, attribute.length());
                attribute = attribute.substring(0, i);
            }

            // TODO: resolve absolute addresses

            tasks.add(new Task(Address.apply(ref.getAddress()), attribute, subref, ref.getInterval()));
        }
        return tasks;
    }

    void stop() {
        this.completionHandler.shutdown();
        this.scheduler.shutdown();
        this.reporter.stop();
        this.reporter.report();
    }

    @Override
    public void onChange() {
        // shutdown scheduler
        // recalculate tasks
        // restart scheduler
    }

    public void reportEvery(int period, TimeUnit unit) {
        if(!started)
            reporter.start(period, unit);
    }





}
