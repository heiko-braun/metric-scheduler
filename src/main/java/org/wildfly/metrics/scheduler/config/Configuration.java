package org.wildfly.metrics.scheduler.config;

import java.util.List;

/**
 * @author Heiko Braun
 * @since 13/10/14
 */
public interface Configuration {

    /**
     * The remote domain controller host.
     * @return
     */
    String getHost();

    /**
     * The remote domain controller port.
     *
     * @return
     */
    int getPort();

    /**
     * Number of threads the scheduler uses to poll for new data.
     *
     * @return
     */
    int getSchedulerThreads();

    /**
     * The resources that are to be monitored.
     * {@link org.wildfly.metrics.scheduler.config.ResourceRef}'s use relative addresses.
     * The core {@link org.wildfly.metrics.scheduler.Service} will resolve it against absolute address within a Wildfly domain.
     *
     * @return
     */
    List<ResourceRef> getResourceRefs();

    String getInfluxUrl();

    String getInfluxUser();

    String getInfluxPassword();

    String getInfluxDBName();

    String getRHQUrl();
}
