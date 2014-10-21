package org.wildfly.metrics.scheduler.storage;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.rhq.metrics.client.common.Batcher;
import org.rhq.metrics.client.common.SingleMetric;
import org.wildfly.metrics.scheduler.config.Configuration;
import org.wildfly.metrics.scheduler.diagnose.Diagnostics;
import org.wildfly.metrics.scheduler.polling.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Pushes the data to RHQ metrics.
 *
 * @author Heiko Braun
 * @since 13/10/14
 */
public class RHQStorageAdapter implements StorageAdapter {

    private Configuration config;
    private Diagnostics diagnostics;
    private HttpClient httpclient = new DefaultHttpClient();


    @Override
    public void setConfiguration(Configuration config) {
        this.config = config;
    }

    @Override
    public void setDiagnostics(Diagnostics diag) {
        this.diagnostics = diag;
    }

    @Override
    public void store(Set<Sample> samples) {
        HttpPost post = new HttpPost(config.getStorageUrl());
        try {
            List<SingleMetric> metrics = new ArrayList<>();

            for (Sample sample : samples) {
                Task task = sample.getTask();
                String source = task.getHost()+"."+task.getServer()+"."+task.getAttribute();
                metrics.add(new SingleMetric(source, sample.getTimestamp(), sample.getValue()));
            }


            // If we have data, send it to the RHQ Metrics server

            if (metrics.size()>0) {
                post.setHeader("Content-Type", "application/json;charset=utf-8");
                post.setEntity(new StringEntity(Batcher.metricListToJson(metrics)));

                HttpResponse httpResponse = httpclient.execute(post);
                StatusLine statusLine = httpResponse.getStatusLine();

                if (statusLine.getStatusCode() != 200) {
                    throw new Exception("HTTP Status "+statusLine.getStatusCode()+": "+statusLine);
                }


            }
        } catch (Throwable t) {
            t.printStackTrace();
            diagnostics.getStorageErrorRate().mark(1);
        }
        finally {
            post.releaseConnection();
        }

    }
}
