package org.jboss.metrics.agenda;

import static java.util.Arrays.asList;

import org.junit.Test;

public class AgendaTest {

    @Test(expected = UnsupportedOperationException.class)
    public void readonly() {
        /*Configuration agenda = new Configuration("test", asList(ConfigurationBuilder.fooTask()));
        agenda.getResourceRefs().remove(0);*/
    }
}