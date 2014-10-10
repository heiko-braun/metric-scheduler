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

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.*;

/**
 * @author Harald Pehl
 */
public enum Interval{

    EACH_SECOND(1, SECONDS),
    TWO_SECONDS(2, SECONDS),
    FIVE_SECONDS(5, SECONDS),

    EACH_MINUTE(1, MINUTES),
    TWO_MINUTES(2, MINUTES),
    FIVE_MINUTES(5, MINUTES),
    TEN_MINUTES(10, MINUTES),
    TWENTY_MINUTES(20, MINUTES),
    THIRTY_MINUTES(30, MINUTES),

    EACH_HOUR(1, TimeUnit.HOURS),
    TWO_HOURS(2, TimeUnit.HOURS),
    SIX_HOURS(6, TimeUnit.HOURS),
    TWELVE_HOURS(12, TimeUnit.HOURS),

    EACH_DAY(1, TimeUnit.DAYS);

    private final int val;
    private final TimeUnit unit;

    Interval(int val, TimeUnit unit) {
        this.val = val;
        this.unit = unit;
    }

    public long millis() {
        return MILLISECONDS.convert(val, unit);
    }
}
