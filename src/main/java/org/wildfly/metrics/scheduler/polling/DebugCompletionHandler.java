package org.wildfly.metrics.scheduler.polling;

import org.jboss.dmr.ModelNode;

public class DebugCompletionHandler implements Scheduler.CompletionHandler {
    @Override
    public void onCompleted(Task t, ModelNode data) {
        System.out.println(t + " > "+ data);
    }

    @Override
    public void onFailed(TaskGroup g, Throwable e) {
        System.out.println("TaskGroup failed: "+e.getMessage());
        e.printStackTrace();
    }
}