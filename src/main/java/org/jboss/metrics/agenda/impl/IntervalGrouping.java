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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimaps;
import org.jboss.metrics.agenda.Interval;
import org.jboss.metrics.agenda.ResourceRef;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskGroup;
import org.jboss.metrics.agenda.TaskGrouping;

/**
 * @author Harald Pehl
 */
public class IntervalGrouping implements TaskGrouping {

    @Override
    public Set<TaskGroup> apply(final Set<Task> tasks) {
        ImmutableListMultimap<Interval, Task> tasksByInterval = Multimaps
                .index(tasks, new Function<Task, Interval>() {
                    @Override
                    public Interval apply(final Task task) {
                        return task.getInterval();
                    }
                });

        Set<TaskGroup> groups = new HashSet<>();
        for (Interval interval : tasksByInterval.keys()) {
            TaskGroup group = new TaskGroup(interval);
            group.addTasks(tasksByInterval.get(interval));
            groups.add(group);
        }
        return groups;
    }
}
