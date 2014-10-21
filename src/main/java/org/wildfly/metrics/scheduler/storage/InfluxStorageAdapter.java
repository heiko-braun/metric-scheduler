package org.wildfly.metrics.scheduler.storage;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;
import org.wildfly.metrics.scheduler.config.Configuration;
import org.wildfly.metrics.scheduler.diagnose.Diagnostics;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Pushes the data to Influx.
 *
 * @author Heiko Braun
 * @since 13/10/14
 */
public class InfluxStorageAdapter implements StorageAdapter {

    private InfluxDB influxDB;
    private String dbName;
    private Diagnostics diagnostics;
    private Configuration config;

    @Override
    public void setConfiguration(Configuration config) {
        this.config = config;
        this.influxDB = InfluxDBFactory.connect(
                config.getStorageUrl(),
                config.getStorageUser(),
                config.getStoragePassword()
        );
        this.dbName = config.getStorageDBName();
    }

    @Override
    public void setDiagnostics(Diagnostics diag) {
        this.diagnostics = diag;
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
            diagnostics.getStorageErrorRate().mark(1);
            t.printStackTrace();
        }

    }
}
