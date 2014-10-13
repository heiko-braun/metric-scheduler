package org.wildfly.metrics.scheduler.storage;

import org.jboss.dmr.ModelNode;
import org.wildfly.metrics.scheduler.polling.Scheduler;
import org.wildfly.metrics.scheduler.polling.Task;
import org.wildfly.metrics.scheduler.polling.TaskGroup;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Heiko Braun
 * @since 13/10/14
 */
public class BufferedStorageDispatcher implements Scheduler.CompletionHandler {

    private final StorageAdapter storageAdapter;
    private final BlockingQueue<Tuple> queue;
    private final Worker worker;

    public BufferedStorageDispatcher(StorageAdapter storageAdapter) {
        this.storageAdapter = storageAdapter;
        this.queue = new ArrayBlockingQueue<Tuple>(100);
        this.worker = new Worker(queue);
    }

    public void start() {
        worker.start();
    }

    public void shutdown() {
        worker.setKeepRunnig(false);
    }

    @Override
    public void onCompleted(Task t, ModelNode data) {
        queue.add(new Tuple(t, data));
    }

    @Override
    public void onFailed(TaskGroup g, Throwable e) {
        e.printStackTrace();
    }

    /**
     * The actual bulk of the work:
     * Turns the data into actual metrics and pushes them to a storage adapter.
     *
     * @param tuple
     */
    private void processTuple(Tuple tuple) {
        Task t = tuple.task;
        ModelNode data = tuple.data;

        //System.out.println(t + " > "+ data);

        String value = null;
        if(t.getSubref()!=null)
        {
            value = data.get("result").get(t.getSubref()).asString();
        }
        else
        {
            value = data.get("result").asString();
        }

        System.out.println(t.getAttribute() + " > "+ value);
        storageAdapter.store(t, value);
    }

    class Tuple {
        final Task task;
        final ModelNode data;

        Tuple(Task t, ModelNode d) {
            this.task = t;
            this.data = d;
        }
    }

    public class Worker extends Thread {
        private final BlockingQueue<Tuple> queue;
        private boolean keepRunning = true;

        public Worker(BlockingQueue<Tuple> queue) {
            this.queue = queue;
        }

        public void run() {
            try {
                while ( keepRunning ) {
                    Tuple tuple = queue.take();
                    processTuple(tuple);
                }
            }
            catch ( InterruptedException ie ) {
                // just terminate
            }
        }

        public void setKeepRunnig(boolean keepRunning) {
            this.keepRunning = keepRunning;
        }
    }
}

