package org.wildfly.metrics.scheduler.storage;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.wildfly.metrics.scheduler.config.Configuration;

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
    private final String dbName;

    public InfluxStorageAdapter(Configuration config) {
        this.influxDB = InfluxDBFactory.connect(
                config.getInfluxUrl(),
                config.getInfluxUser(),
                config.getInfluxPassword()
        );
        this.dbName = config.getInfluxDBName();
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

            this.influxDB.write(this.dbName, TimeUnit.MILLISECONDS, series);

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }
}
