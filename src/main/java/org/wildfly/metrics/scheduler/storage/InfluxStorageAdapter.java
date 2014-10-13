package org.wildfly.metrics.scheduler.storage;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.wildfly.metrics.scheduler.StorageAdapter;
import org.wildfly.metrics.scheduler.polling.Task;

import java.util.concurrent.TimeUnit;

/**
 * @author Heiko Braun
 * @since 13/10/14
 */
public class InfluxStorageAdapter implements StorageAdapter {

    private final InfluxDB influxDB;

    public InfluxStorageAdapter() {
        this.influxDB = InfluxDBFactory.connect("http://sandbox.influxdb.com:8086", "admin", "password123");
    }

    @Override
    public void store(Task task, String value) {

        try {

            Serie dataPoints = new Serie.Builder(task.getAttribute())
                    .columns("sample")
                    .values(Double.valueOf(value))
                    .build();

            this.influxDB.write("wildfly", TimeUnit.MILLISECONDS, dataPoints);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
