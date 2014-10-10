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
package org.jboss.metrics.agenda.impl;

import com.google.common.collect.Iterators;
import org.jboss.metrics.agenda.cfg.Interval;
import org.jboss.metrics.agenda.Task;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * @author Harald Pehl
 */
class TaskGroup implements Iterable<Task> {

    private final String id; // to uniquely reference this group
    private final Interval interval; // impacts thread scheduling
    private final long offsetMillis;
    private final Set<Task> tasks;

    public TaskGroup(final Interval interval) {
        this.offsetMillis = 0;
        this.id = UUID.randomUUID().toString();
        this.interval = interval;
        this.tasks = new HashSet<>();
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

    public long getOffsetMillis() {
        return offsetMillis;
    }
}