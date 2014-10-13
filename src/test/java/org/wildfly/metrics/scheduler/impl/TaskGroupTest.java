package org.wildfly.metrics.scheduler.impl;

import org.junit.Test;
import org.wildfly.metrics.scheduler.cfg.Address;
import org.wildfly.metrics.scheduler.cfg.Interval;

import static org.wildfly.metrics.scheduler.cfg.Interval.EACH_SECOND;

public class TaskGroupTest {

    @Test(expected = UnsupportedOperationException.class)
    public void readonly() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(new Task(Address.apply("/foo=bar"), "attribute", Interval.EACH_SECOND));
        group.iterator().remove();
    }

    @Test(expected = IllegalArgumentException.class)
    public void interval() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(new Task(Address.apply("/foo=bar"), "attribute", Interval.EACH_DAY));
    }
}