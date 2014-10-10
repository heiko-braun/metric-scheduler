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
import java.util.Set;

import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.Operation;
import org.jboss.metrics.agenda.OperationBuilder;
import org.jboss.metrics.agenda.ResourceRef;
import org.jboss.metrics.agenda.Task;
import org.jboss.metrics.agenda.TaskGroup;
import org.jboss.metrics.agenda.address.Address;

/**
 * Creates a {@code read-attribute} operation of the given {@link org.jboss.metrics.agenda.TaskGroup}.
 *
 * @author Harald Pehl
 */
public class ReadAttributeOperationBuilder implements OperationBuilder {

    @Override
    public Set<Operation> createOperation(final TaskGroup group) {

        if (group.isEmpty()) {
            throw new IllegalArgumentException("Empty groups are not allowed");
        }


        HashSet<Operation> operations = new HashSet<>();
        for (Task t : group) {
            ModelNode node = readAttribute(t);
            operations.add(new Operation(group.getInterval().millis(), node));
        }

        return operations;

      /*  if (group.size() == 1) {
            ModelNode node = readAttribute(group.iterator().next());
            return new HashSet<>(asList(new Operation(group.getInterval().millis(), node)));

        } else {
            ModelNode comp = new ModelNode();
            List<ModelNode> steps = new ArrayList<>();
            comp.get("address").setEmptyList();
            comp.get("operation").set("composite");
            for (Task task : group) {
                steps.add(readAttribute(task));
            }
            comp.get("steps").set(steps);
            return new HashSet<>(asList(new Operation(group.getInterval().millis(), comp)));
        }*/
    }

    private ModelNode readAttribute(Task task) {
        ModelNode node = new ModelNode();
        Address address = task.getAddress();
        for (Address.Tuple tuple : address) {
            node.get("address").add(tuple.getKey(), tuple.getValue());
        }
        node.get("operation").set("read-attribute");
        node.get("name").set(task.getAttribute());
        return node;
    }
}
