package org.wildfly.metrics.scheduler.polling;

import org.jboss.dmr.ModelNode;
import org.wildfly.metrics.scheduler.TaskCompletionHandler;

public class DebugCompletionHandler implements TaskCompletionHandler<ModelNode> {
    @Override
    public void onCompleted(Task t, ModelNode data) {
        System.out.println(t + " > "+ data);
    }

    @Override
    public void onFailed(TaskGroup g, Throwable e) {
        System.out.println("TaskGroup failed: "+e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void start() {
        // noop
    }

    @Override
    public void shutdown() {
        // noop
    }
}