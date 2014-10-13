package org.wildfly.metrics.scheduler.impl;

import org.wildfly.metrics.scheduler.cfg.Address;
import org.wildfly.metrics.scheduler.cfg.Interval;

/**
 * Represents a monitoring task.
 *
 * @author Heiko Braun
 * @since 10/10/14
 */
public class Task {

    private final Address address;  // absolute address
    private final String attribute;
    private final Interval interval;

    public Task(Address address, String attribute, Interval interval) {
        this.address = address;
        this.attribute = attribute;
        this.interval = interval;
    }

    public Address getAddress() {
        return address;
    }

    public String getAttribute() {
        return attribute;
    }

    public Interval getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "Task{" +
                "address=" + address +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
