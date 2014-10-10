/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.metrics.agenda;

import com.google.common.collect.Iterators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * @author Harald Pehl
 */
public class TaskGroup implements Iterable<Task> {

    public final static String ANY_HOST = TaskGroup.class.getName() + ".anyHost";
    public final static String ANY_SERVER = TaskGroup.class.getName() + ".anyServer";

    private final String id; // to uniquely reference this group
    private final Interval interval; // impacts thread scheduling
    private final String host; // impacts DC - HC communication
    private final String server; // impacts DC - HC communication
    private final Set<Task> tasks;

    public TaskGroup(final Interval interval) {
        this(interval, ANY_HOST, ANY_SERVER);
    }

    public TaskGroup(final Interval interval, String host) {
        this(interval, host, ANY_SERVER);
    }

    public TaskGroup(final Interval interval, final String host, final String server) {
        this.id = UUID.randomUUID().toString();
        this.interval = interval;
        this.host = host;
        this.server = server;
        this.tasks = new HashSet<>();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof TaskGroup)) { return false; }

        TaskGroup taskGroup = (TaskGroup) o;

        if (!host.equals(taskGroup.host)) { return false; }
        if (interval != taskGroup.interval) { return false; }
        if (!server.equals(taskGroup.server)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = interval.hashCode();
        result = 31 * result + host.hashCode();
        result = 31 * result + server.hashCode();
        return result;
    }

    public void addTask(Task task) {
        verifyInterval(task);
        tasks.add(task);
    }

    public boolean addTasks(final Collection<? extends Task> collection) {
        for (Task t: collection) {
            verifyInterval(t);
        }
        return tasks.addAll(collection);
    }

    private void verifyInterval(final Task task) {
        if (task.getInterval() != interval) {
            throw new IllegalArgumentException("Wrong interval: Expected " + interval + ", but got " + task.getInterval());
        }
    }

    public int size() {return tasks.size();}

    public boolean isEmpty() {return tasks.isEmpty();}

    @Override
    public Iterator<Task> iterator() {
        return Iterators.unmodifiableIterator(tasks.iterator());
    }

    public String getId() {
        return id;
    }

    public Interval getInterval() {
        return interval;
    }

    public String getHost() {
        return host;
    }

    public String getServer() {
        return server;
    }
}
