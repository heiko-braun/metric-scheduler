package org.wildfly.metrics.scheduler.report;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Timer;

/**
 * @author Heiko Braun
 * @since 13/10/14
 */
public interface Monitor {
    Timer getRequestTimer();
    Meter getErrorRate();
    Meter getDelayedRate();
}
