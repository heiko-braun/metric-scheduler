package org.jboss.metrics.scheduler;

import org.junit.Test;

public class AgendaTest {

    @Test(expected = UnsupportedOperationException.class)
    public void readonly() {
        /*Configuration agenda = new Configuration("test", asList(ConfigurationBuilder.fooTask()));
        agenda.getResourceRefs().remove(0);*/
    }
}