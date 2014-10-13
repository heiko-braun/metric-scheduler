package org.wildfly.metrics.scheduler;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Timer;

/**
 * @author Heiko Braun
 * @since 13/10/14
 */
public interface Monitor {
    Timer getRequestTimer();
    Counter getDelayedCounter();
}
