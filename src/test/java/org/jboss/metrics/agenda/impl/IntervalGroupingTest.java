package org.jboss.metrics.agenda.impl;

import static org.jboss.metrics.agenda.Interval.*;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Set;

import org.jboss.metrics.agenda.ResourceRef;

import org.junit.Before;
import org.junit.Test;

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