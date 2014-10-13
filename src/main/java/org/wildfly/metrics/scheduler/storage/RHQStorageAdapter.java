package org.wildfly.metrics.scheduler.storage;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.rhq.metrics.client.common.Batcher;
import org.rhq.metrics.client.common.SingleMetric;
import org.wildfly.metrics.scheduler.StorageAdapter;
import org.wildfly.metrics.scheduler.polling.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @since 13/10/14
 */
public class RHQStorageAdapter implements StorageAdapter {

    private HttpClient httpclient = new DefaultHttpClient();

    @Override
    public void store(Task task, String value) {


        try {
            String source = "localhost";
            long now = System.currentTimeMillis();

            List<SingleMetric> metrics = new ArrayList<>();
            metrics.add(new SingleMetric(source, now, Double.valueOf(value)));

            // If we have data, send it to the RHQ Metrics server
            if (metrics.size()>0) {
                HttpPost post = new HttpPost("http://localhost:8080/rhq-metrics/metrics"); // TODO
                post.setHeader("Content-Type", "application/json;charset=utf-8");
                post.setEntity(new StringEntity(Batcher.metricListToJson(metrics)));

                HttpResponse httpResponse = httpclient.execute(post);
                StatusLine statusLine = httpResponse.getStatusLine();

                if (statusLine.getStatusCode() != 200) {
                    System.err.println(" +-- Result status is " + statusLine);
                }

                post.releaseConnection();
            }
        } catch (IllegalArgumentException | IOException iae) {
            iae.printStackTrace();
        }

    }
}