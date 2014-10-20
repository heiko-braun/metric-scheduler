package org.wildfly.metrics.scheduler.polling;

import org.junit.Before;
import org.junit.Test;
import org.wildfly.metrics.scheduler.config.Address;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.wildfly.metrics.scheduler.config.Interval.*;

public class IntervalGroupingTest {

    private IntervalGrouping grouping;
    @Before
    public void setUp() {
        grouping = new IntervalGrouping();
    }

    @Test
    public void apply() {
        Task s1x = new Task("host", "server", Address.apply("/a=b"), "x", null, EACH_SECOND);
        Task s1y = new Task("host", "server", Address.apply("/a=b"), "x", null, EACH_SECOND);
        Task s1z = new Task("host", "server", Address.apply("/a=b"), "x", null, EACH_SECOND);

        Task s2x = new Task("host", "server", Address.apply("/a=b"), "x", null, TWENTY_SECONDS);
        Task s2y = new Task("host", "server", Address.apply("/a=b"), "x", null, TWENTY_SECONDS);

        Task m1x = new Task("host", "server", Address.apply("/a=b"), "x", null, EACH_DAY);
        Task m1y = new Task("host", "server", Address.apply("/a=b"), "x", null, EACH_DAY);
        Task m1z = new Task("host", "server", Address.apply("/a=b"), "x", null, EACH_DAY);

        List<TaskGroup> groups = grouping.apply(Arrays.asList(s1x, s1y, s1z, s2x, s2y, m1x, m1y, m1z));

        assertEquals(3, groups.size());
    }
}