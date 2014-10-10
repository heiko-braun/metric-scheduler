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
package org.wildfly.metrics.scheduler;

import org.wildfly.metrics.scheduler.Task;
import org.wildfly.metrics.scheduler.cfg.Address;
import org.wildfly.metrics.scheduler.cfg.ConfigLoader;
import org.wildfly.metrics.scheduler.cfg.Configuration;
import org.wildfly.metrics.scheduler.cfg.Interval;
import org.wildfly.metrics.scheduler.cfg.ResourceRef;

import static org.wildfly.metrics.scheduler.cfg.Interval.EACH_SECOND;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Harald Pehl
 */
public final class TestConfiguration implements ConfigLoader {


    public static Task fooTask() {
        return fooTask(EACH_SECOND);
    }

    public static Task fooTask(Interval interval) {
        return new Task(Address.apply("/foo=bar"), "baz", interval);
    }

   /* public static Agenda dataSourceAgenda() {
        List<Task> definitions = new ArrayList<>();
        String address = "/subsystem=datasources/data-source=ExampleDS/statistics=pool";

        definitions.add(new ResourceRef(address, "CreatedCount", EACH_SECOND));
            definitions.add(new ResourceRef(address, "DestroyedCount", EACH_SECOND));

            definitions.add(new ResourceRef(address, "TimedOut", Interval.TWO_SECONDS));
            definitions.add(new ResourceRef(address, "InUseCount", Interval.TWO_SECONDS));
            definitions.add(new ResourceRef(address, "AverageBlockingTime", Interval.TWO_SECONDS));

            definitions.add(new ResourceRef(address, "AverageCreationTime", Interval.FIVE_SECONDS));
            definitions.add(new ResourceRef(address, "AvailableCount", Interval.FIVE_SECONDS));
            definitions.add(new ResourceRef(address, "ActiveCount", Interval.FIVE_SECONDS));
        return new Agenda("dataSourceAgenda", definitions);
    }*/

    public Configuration load() {
        List<ResourceRef> definitions = new ArrayList<>();


        for(int i=0; i<250; i++) {
            String address = "/subsystem=datasources/data-source=ExampleDS/statistics=pool";

            definitions.add(new ResourceRef(address, "CreatedCount", EACH_SECOND));
            definitions.add(new ResourceRef(address, "DestroyedCount", EACH_SECOND));

            definitions.add(new ResourceRef(address, "TimedOut", Interval.TWO_SECONDS));
            definitions.add(new ResourceRef(address, "InUseCount", Interval.TWO_SECONDS));
            definitions.add(new ResourceRef(address, "AverageBlockingTime", Interval.TWO_SECONDS));

            definitions.add(new ResourceRef(address, "AverageCreationTime", Interval.FIVE_SECONDS));
            definitions.add(new ResourceRef(address, "AvailableCount", Interval.FIVE_SECONDS));
            definitions.add(new ResourceRef(address, "ActiveCount", Interval.FIVE_SECONDS));
        }

        return new Configuration("dataSourceAgenda", "localhost", 9999, definitions);
    }
}
