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
package org.wildfly.metrics.scheduler.impl;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.wildfly.metrics.scheduler.Task;
import org.wildfly.metrics.scheduler.TaskCompletionHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.wildfly.metrics.scheduler.Scheduler.State.RUNNING;
import static org.wildfly.metrics.scheduler.Scheduler.State.STOPPED;

/**
 * @author Harald Pehl
 * @author Heiko Braun
 */
public class IntervalBasedScheduler extends AbstractScheduler {

    private final ScheduledExecutorService executorService;
    private final List<ScheduledFuture> jobs;
    private final ConsoleReporter reporter;
    private final Timer requestTimer;
    private final Counter delayCounter;
    private final int poolSize;
    private final String host;
    private final int port;
    private final TaskCompletionHandler<DMRResponse> completionHandler;

    private ConcurrentLinkedQueue<ModelControllerClient> connectionPool = new ConcurrentLinkedQueue<>();

    public IntervalBasedScheduler(final int poolSize, String host, int port, TaskCompletionHandler<DMRResponse> completionHandler) {

        this.poolSize = poolSize;
        this.host = host;
        this.port = port;
        this.completionHandler = completionHandler;

        this.executorService = Executors.newScheduledThreadPool(poolSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                System.out.println("<< created new executor >>");
                return new Thread(r);
            }
        });

        this.jobs = new LinkedList<>();

        // metrics
        MetricRegistry metrics = new MetricRegistry();
        this.reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(MILLISECONDS)
                .build();

        this.requestTimer = metrics.timer(name("requestTimer"));
        this.delayCounter = metrics.counter(name("delayCounter"));

    }

    @Override
    public void schedule(List<Task> tasks) {
        verifyState(STOPPED);

         // optimize task groups
        List<TaskGroup> groups = new IntervalGrouping().apply(tasks);

        System.out.println("<< Number of Tasks: "+tasks.size()+" >>");
        System.out.println("<< Number of Task Groups: "+groups.size()+" >>");

        // populate connection pool
        for (int i = 0; i < poolSize; i++) {
            try {
                connectionPool.add(
                        ModelControllerClient.Factory.create(InetAddress.getByName(host), port)
                );
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        // schedule IO
        ReadAttributeOperationBuilder operationBuilder = new ReadAttributeOperationBuilder();
        // TODO: with task groups we loose the task reference
        // due to the composite operation
        for (TaskGroup group : groups) {
            jobs.add(

                    // schedule tasks
                    executorService.scheduleWithFixedDelay(
                            new IO(operationBuilder.createOperation(group)),
                            group.getOffsetMillis(), group.getInterval().millis(),
                            MILLISECONDS
                    )
            );
        }

        pushState(RUNNING);
    }

    @Override
    public void shutdown() {
        verifyState(RUNNING);


        try {
            for (ScheduledFuture job : jobs) {
                job.cancel(false);
            }
            executorService.shutdown();
            executorService.awaitTermination(2, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            // cleanup pool
            for (ModelControllerClient client : connectionPool) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            pushState(STOPPED);
            reporter.stop();
            reporter.report();
        }
    }

    private class IO implements Runnable {

        private static final String OUTCOME = "outcome";
        private static final String RESULT = "result";
        private static final String FAILURE_DESCRIPTION = "failure-description";
        private static final String SUCCESS = "success";

        private final DMRRequest operation;

        private IO(final DMRRequest operation) {
            this.operation = operation;
        }

        @Override
        public void run() {
            DMRResponse operationResult = null;
            if(connectionPool.isEmpty())
                throw new IllegalStateException("Connection pool expired!");
            final ModelControllerClient client = connectionPool.poll();

            try {

                Timer.Context requestContext = requestTimer.time();
                delayCounter.inc(); // assumption: every op is delayed or erroneous
                ModelNode response = client.execute(operation.getModelNode());
                long durationMs = requestContext.stop() / 1000000;

                String outcome = response.get(OUTCOME).asString();
                if (SUCCESS.equals(outcome))
                {

                    if (durationMs < operation.getInterval()) {
                        delayCounter.dec(); // not delayed
                    }

                    operationResult = new DMRResponse(
                            operation.getId(),
                            response.get(RESULT),
                            DMRResponse.Status.SUCCESS
                    );

                } else {
                    operationResult = new DMRResponse(
                            operation.getId(),
                            response.get(FAILURE_DESCRIPTION),
                            DMRResponse.Status.FAILED
                    );
                }

            } catch (IOException e) {
                ModelNode exceptionModel = new ModelNode().get(FAILURE_DESCRIPTION).set(e.getMessage());
                operationResult = new DMRResponse(operation.getId(), exceptionModel, DMRResponse.Status.FAILED);
            } finally {

                // return to pool
                connectionPool.add(client);

                if (operationResult != null) {

                    //TODO: pass on task references
                    if(DMRResponse.Status.SUCCESS == operationResult.getStatus())
                        completionHandler.onCompleted(null, operationResult);
                    else
                        completionHandler.onFailed(null, new RuntimeException(operationResult.getErrorDescription()));
                }
            }
        }

    }

}
