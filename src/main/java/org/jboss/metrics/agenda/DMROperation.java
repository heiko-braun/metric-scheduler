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

import org.jboss.dmr.ModelNode;

import java.util.UUID;

/**
 * An executable operation holding an unique id and a {@link org.jboss.dmr.ModelNode} model node.
 *
 * @author Harald Pehl
 */
public class DMROperation {

    private final String id;
    private final long interval;
    private final ModelNode modelNode;

    public DMROperation(final long interval, final ModelNode modelNode) {
        this.interval = interval;
        this.id = UUID.randomUUID().toString();
        this.modelNode = modelNode;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof DMROperation)) { return false; }

        DMROperation operation = (DMROperation) o;

        if (!id.equals(operation.id)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Operation(" + id + ", " + interval + "ms)";
    }

    public String getId() {
        return id;
    }

    public long getInterval() {
        return interval;
    }

    public ModelNode getModelNode() {
        return modelNode;
    }
}
