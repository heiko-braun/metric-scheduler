package org.wildfly.metrics.scheduler;

import org.jboss.dmr.ModelNode;
import org.junit.Test;
import static org.junit.Assert.*;
import org.wildfly.metrics.scheduler.config.ConfigLoader;
import org.wildfly.metrics.scheduler.config.ConfigurationInstance;
import org.wildfly.metrics.scheduler.config.Interval;
import org.wildfly.metrics.scheduler.config.ResourceRef;
import org.wildfly.metrics.scheduler.polling.Task;
import org.wildfly.metrics.scheduler.polling.TaskGroup;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.wildfly.metrics.scheduler.config.Interval.EACH_MINUTE;

/**
 * Test task failure scenarios
 *
 * @author Heiko Braun
 */
public class TaskFailureTest {

    class Counter{
        int completed = 0;
        int failed = 0;
    }


    class ErrorConfig implements ConfigLoader {
        @Override
        public ConfigurationInstance load() {
            List<ResourceRef> definitions = new ArrayList<>();
            String vmAddress = "/core-service=platform-mbean/type=memory";

            definitions.add(new ResourceRef(vmAddress, "heap-memory-usage", EACH_MINUTE));
            definitions.add(new ResourceRef("/foo=bar", "attribute", Interval.EACH_MINUTE));

            return new ConfigurationInstance("localhost", 9999, definitions);
        }
    }

    /**
     * If one task within a group fails, the whole group fails.
     * This is due to the usage of composite operations.
     *
     * @throws Exception
     */
    @Test
    public void testTaskFailureHandling() throws Exception {
       /*  // create configuration
        ConfigurationInstance configuration = new ErrorConfig().load();

        final Counter counter = new Counter();

        // create service
        TaskCompletionHandler<ModelNode> completionHandler = new TaskCompletionHandler<ModelNode>() {
            @Override
            public void onCompleted(Task t, ModelNode data) {
                counter.completed++;
            }

            @Override
            public void onFailed(TaskGroup g, Throwable e) {
                counter.failed++;
            }

            @Override
            public void start() {

            }

            @Override
            public void shutdown() {

            }
        };

        Service service = new Service(configuration, completionHandler);

        service.start();
        SECONDS.sleep(2);
        service.stop();

        assertTrue("Expected one failed task", counter.failed == 1);
        assertTrue("Expected one completed task", counter.completed == 0);*/

    }
}