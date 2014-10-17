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
    private int schedulerThreads = 2;

    private String host;
    private int port;
    private String user;
    private String password;

    private String rhqUrl = "http://localhost:8080/rhq-metrics/metrics";

    private String influxUrl = "http://sandbox.influxdb.com:8086";
    private String influxUser;
    private String influxPassword;
    private String influxDb;

    public ConfigurationInstance(String host, int port) {
        this(host, port, new ArrayList<ResourceRef>());
    }

    public ConfigurationInstance(String host, int port, final List<ResourceRef> resourceRefs) {
        this.host = host;
        this.port = port;
        this.resourceRefs = resourceRefs;
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
    public String getUsername() {
        return this.user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setSchedulerThreads(int schedulerThreads) {
        this.schedulerThreads = schedulerThreads;
    }

    @Override
    public int getSchedulerThreads() {
        return schedulerThreads;
    }

    @Override
    public String getInfluxUrl() {
        return this.influxUrl;
    }

    public void setInfluxUrl(String influxUrl) {
        this.influxUrl = influxUrl;
    }

    @Override
    public String getInfluxUser() {
        return influxUser;
    }

    @Override
    public String getInfluxPassword() {
        return influxPassword;
    }

    @Override
    public String getInfluxDBName() {
        return influxDb;
    }

    public void setInfluxUser(String influxUser) {
        this.influxUser = influxUser;
    }

    public void setInfluxPassword(String influxPassword) {
        this.influxPassword = influxPassword;
    }

    public void setInfluxDb(String influxDb) {
        this.influxDb = influxDb;
    }

    @Override
    public String getRHQUrl() {
        return rhqUrl;
    }

    public void setRhqUrl(String rhqUrl) {
        this.rhqUrl = rhqUrl;
    }

    public void addResourceRef(ResourceRef ref) {
        this.resourceRefs.add(ref);
    }

}

