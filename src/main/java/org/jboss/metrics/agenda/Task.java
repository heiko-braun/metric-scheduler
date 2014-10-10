package org.jboss.metrics.agenda;

import org.jboss.metrics.agenda.cfg.Address;
import org.jboss.metrics.agenda.cfg.Interval;

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

    Task(Address address, String attribute, Interval interval) {
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
