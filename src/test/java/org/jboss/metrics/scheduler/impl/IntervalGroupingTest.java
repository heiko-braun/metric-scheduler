package org.jboss.metrics.scheduler.impl;

import static org.wildfly.metrics.scheduler.cfg.Interval.*;
import static org.junit.Assert.assertEquals;

import org.wildfly.metrics.scheduler.cfg.ResourceRef;

import org.junit.Before;
import org.junit.Test;
import org.wildfly.metrics.scheduler.impl.IntervalGrouping;

public class IntervalGroupingTest {

    private IntervalGrouping grouping;
    @Before
    public void setUp() {
        grouping = new IntervalGrouping();
    }

    @Test
    public void apply() {
        ResourceRef s1x = new ResourceRef("/a=b", "x", EACH_SECOND);
        ResourceRef s1y = new ResourceRef("/a=b", "y", EACH_SECOND);
        ResourceRef s1z = new ResourceRef("/a=b", "z", EACH_SECOND);

        ResourceRef s2x = new ResourceRef("/a=b", "x", TWO_SECONDS);
        ResourceRef s2y = new ResourceRef("/a=b", "y", TWO_SECONDS);

        ResourceRef m1x = new ResourceRef("/a=b", "x", EACH_MINUTE);
        ResourceRef m1y = new ResourceRef("/a=b", "y", EACH_MINUTE);
        ResourceRef m1z = new ResourceRef("/a=b", "z", EACH_MINUTE);

        /*Set<TaskGroup> groups = grouping.apply(Arrays.asList(s1x, s1y, s1z, s2x, s2y, m1x, m1y, m1z));

        assertEquals(3, groups.size());*/
    }
}