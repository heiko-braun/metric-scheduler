package org.wildfly.metrics.scheduler.storage;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Pushes the data to Influx.
 *
 * @author Heiko Braun
 * @since 13/10/14
 */
public class InfluxStorageAdapter implements StorageAdapter {

    private final InfluxDB influxDB;

    public InfluxStorageAdapter() {
        this.influxDB = InfluxDBFactory.connect("http://sandbox.influxdb.com:8086", "admin", "password123");
    }

    @Override
    public void store(Set<Sample> samples) {

        try {

            Serie[] series = new Serie[samples.size()];
            int i=0;
            for (Sample sample : samples) {
                Serie dataPoint = new Serie.Builder(sample.getTask().getAttribute())
                        .columns("sample")
                        .values(sample.getValue())
                        .build();

                series[i] = dataPoint;
                i++;
            }

            this.influxDB.write("wildfly", TimeUnit.MILLISECONDS, series);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
