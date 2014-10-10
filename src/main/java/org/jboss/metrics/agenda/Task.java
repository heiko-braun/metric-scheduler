package org.jboss.metrics.agenda;

import org.jboss.metrics.agenda.address.Address;

/**
 * @author Heiko Braun
 * @since 10/10/14
 */
public class Task {

    private final Address address;
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
}
