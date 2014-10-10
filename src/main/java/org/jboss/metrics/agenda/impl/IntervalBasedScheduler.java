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
package org.jboss.metrics.agenda.impl;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.dmr.ModelNode;
import org.jboss.metrics.agenda.DMROperation;
import org.jboss.metrics.agenda.OperationResult;
import org.jboss.metrics.agenda.OperationResultConsumer;
import org.jboss.metrics.agenda.Statistics;
import org.jboss.metrics.agenda.Task;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.codahale.metrics.MetricRegistry.name;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.jboss.metrics.agenda.Scheduler.State.RUNNING;
import static org.jboss.metrics.agenda.Scheduler.State.STOPPED;

/**
 * @author Harald Pehl
 */
public class IntervalBasedScheduler extends AbstractScheduler {

    private final ScheduledExecutorService executorService;
    private final List<ScheduledFuture> jobs;
    private final OperationResultConsumer consumer;
    private final ConsoleReporter reporter;
    private final Timer stopwatch;
    private final Counter delayed;
    private final int poolSize;

    private ConcurrentLinkedQueue<ModelControllerClient> connectionPool = new ConcurrentLinkedQueue<>();

    public IntervalBasedScheduler(final int poolSize, final OperationResultConsumer consumer) {

        this.poolSize = poolSize;
        this.executorService = Executors.newScheduledThreadPool(poolSize, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                System.out.println("<< created new executor >>");
                return new Thread(r);
            }
        });

        this.jobs = new LinkedList<>();
        this.consumer = consumer;

        // metrics
        MetricRegistry metrics = new MetricRegistry();
        this.reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(MILLISECONDS)
                .build();

        this.stopwatch = metrics.timer(name("stopwatch"));
        this.delayed = metrics.counter(name("delayed"));

    }

    @Override
    public void schedule(List<Task> tasks) {
        verifyState(STOPPED);

         // optimize task groups
        List<TaskGroup> groups = new IntervalGrouping().apply(tasks);

        // create IO blocks
        Set<DMROperation> operations = new HashSet<>();
        ReadAttributeOperationBuilder operationBuilder = new ReadAttributeOperationBuilder();
        for (TaskGroup group : groups) {
            operations.add(operationBuilder.createOperation(group));
        }


        System.out.println("<< Number of Tasks: "+tasks.size()+" >>");
        System.out.println("<< Number of Task Groups: "+groups.size()+" >>");
        System.out.println("<< Number of Operations: "+operations.size()+" >>");

        // populate pool
        for (int i = 0; i < poolSize; i++) {
            try {
                connectionPool.add(
                        ModelControllerClient.Factory.create(InetAddress.getByName("localhost"), 9999)
                );
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        // schedule jobs
        for (DMROperation operation : operations) {
            jobs.add(

                    // schedule tasks
                    executorService.scheduleWithFixedDelay(
                            new IO(operation),
                            0, operation.getInterval(),
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

    @Override
    public Statistics currentStats() {
        return null;
    }

    private class IO implements Runnable {

        private final DMROperation operation;

        private IO(final DMROperation operation) {
            this.operation = operation;
        }

        @Override
        public void run() {
            OperationResult operationResult = null;
            if(connectionPool.isEmpty())
                throw new IllegalStateException("Connection pool expired!");
            final ModelControllerClient client = connectionPool.poll();

            try {

                Timer.Context context = stopwatch.time();
                delayed.inc(); // assumption: every op is delayed or erroneous
                ModelNode response = client.execute(operation.getModelNode());
                long durationMs = context.stop() / 1000000;

                String outcome = response.get("outcome").asString();
                if ("success".equals(outcome)) {
                    if (durationMs < operation.getInterval()) {
                        delayed.dec(); // not delayed
                    }
                    operationResult = new OperationResult(operation.getId(), response.get("result"),
                            OperationResult.Status.SUCCESS);
                } else {
                    operationResult = new OperationResult(operation.getId(), response.get("failure-description"),
                            OperationResult.Status.FAILED);
                }
            } catch (IOException e) {
                ModelNode exceptionModel = new ModelNode().get("failure-description").set(e.getMessage());
                operationResult = new OperationResult(operation.getId(), exceptionModel, OperationResult.Status.FAILED);
            } finally {

                // return to pool
                connectionPool.add(client);

                if (operationResult != null && consumer != null) {
                    consumer.consume(operationResult);
                }
            }
        }

    }

}
