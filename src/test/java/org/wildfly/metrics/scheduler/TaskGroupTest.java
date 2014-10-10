package org.wildfly.metrics.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TaskGroupTest {

  /*  @Test
    public void defaults() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        assertEquals(TaskGroup.ANY_HOST, group.getHost());
        assertEquals(TaskGroup.ANY_SERVER, group.getServer());
    }

    @Test
    public void equals() {
        TaskGroup g1 = new TaskGroup(EACH_SECOND);
        TaskGroup g2 = new TaskGroup(EACH_SECOND);
        assertEquals(g1, g2);
        assertNotEquals(g1.getId(), g2.getId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readonly() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(ConfigurationBuilder.fooTask(EACH_SECOND));
        group.iterator().remove();
    }

    @Test(expected = IllegalArgumentException.class)
    public void interval() {
        TaskGroup group = new TaskGroup(EACH_SECOND);
        group.addTask(ConfigurationBuilder.fooTask(EACH_MINUTE));
    }*/
}