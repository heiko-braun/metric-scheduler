package org.wildfly.metrics.scheduler.polling;

import org.wildfly.metrics.scheduler.config.Address;
import org.wildfly.metrics.scheduler.config.Interval;

/**
 * Represents a monitoring task.
 *
 * @author Heiko Braun
 * @since 10/10/14
 */
public class Task {

    private final Address address;  // absolute address
    private final String attribute;
    private final String subref;
    private final Interval interval;

    public Task(Address address, String attribute, String subref, Interval interval) {
        this.address = address;
        this.attribute = attribute;
        this.subref = subref;
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

    public String getSubref() {
        return subref;
    }

    @Override
    public String toString() {
        return "Task{" +
                "address=" + address +
                ", attribute='" + attribute + '\'' +
                '}';
    }
}
