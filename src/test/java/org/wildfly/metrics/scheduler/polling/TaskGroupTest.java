package org.wildfly.metrics.scheduler.polling;

import org.junit.Test;
import org.wildfly.metrics.scheduler.config.Address;
import org.wildfly.metrics.scheduler.config.Interval;

import static org.wildfly.metrics.scheduler.config.Interval.EACH_SECOND;

public class TaskGroupTest {

    @Test(expected = UnsupportedOperationException.class)
    public void readonly() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(new Task(Address.apply("/foo=bar"), "attribute", null, Interval.EACH_SECOND));
        group.iterator().remove();
    }

    @Test(expected = IllegalArgumentException.class)
    public void interval() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(new Task(Address.apply("/foo=bar"), "attribute", null, Interval.EACH_DAY));
    }
}