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
package org.wildfly.metrics.scheduler.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A collection of {@link ResourceRef}s with a unique id.
 *
 * @author Harald Pehl
 */
public class ConfigurationInstance implements Configuration {

    private final List<ResourceRef> resourceRefs;
    private String host;
    private int port;
    private int schedulerThreads = 2;

    public ConfigurationInstance(String host, int port, final List<ResourceRef> resourceRefs) {
        this.host = host;
        this.port = port;
        this.resourceRefs = new ArrayList<>();

        if (resourceRefs != null) {
            this.resourceRefs.addAll(resourceRefs);
        }
    }

    public List<ResourceRef> getResourceRefs() {
        return Collections.unmodifiableList(resourceRefs);
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getSchedulerThreads() {
        return schedulerThreads;
    }
}
