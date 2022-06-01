package org.task;

import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Calendar;
import java.util.List;
import java.util.Random;
public class DeviceTemperatureFakeSource extends RichParallelSourceFunction<TemperatureEvent> {

    private final List<String> devices;
    private final Random rand = new Random();

    private boolean running = true;

    public DeviceTemperatureFakeSource(List<String> devices) {
        this.devices = devices;
    }

    @Override
    public void run(SourceContext<TemperatureEvent> srcCtx) throws Exception {
        while (running) {
            devices.forEach(deviceId -> {
                double currentTemperature = rand.nextGaussian() * 3 + 50;
                long currentTime = Calendar.getInstance().getTimeInMillis();
                srcCtx.collect(new TemperatureEvent(deviceId, currentTemperature, currentTime));
            });
            Thread.sleep(600);
        }
    }

    @Override
    public void cancel() {
        this.running = false;
    }
}
