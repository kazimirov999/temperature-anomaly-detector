package org.task;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static final int DEVICE_COUNT = 2000;
    private static final double Z_SCORE_BOUNDARY = 3.0;
    private static final List<String> DEVICE_IDS = generateDevices();

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        env.addSource(new DeviceTemperatureFakeSource(DEVICE_IDS))
                .setParallelism(1)
                .assignTimestampsAndWatermarks(WatermarkStrategy.<TemperatureEvent>forMonotonousTimestamps()
                        .withTimestampAssigner((temperatureEvent, l) -> temperatureEvent.timestamp))
                .keyBy(temperatureEvent -> temperatureEvent.deviceId)
                .window(SlidingEventTimeWindows.of(Time.minutes(1), Time.seconds(30)))
                .apply(new TemperatureOutlierExtractor())
                .print();

        env.execute("Detect temperature anomalies");
    }

    public static class TemperatureOutlierExtractor implements WindowFunction<TemperatureEvent, TemperatureEvent, String, TimeWindow> {
        @Override
        public void apply(String deviceId,
                          TimeWindow window,
                          Iterable<TemperatureEvent> input,
                          Collector<TemperatureEvent> out) {
            OutlierDetectionUtil
                    .detectOutlierTemperatureEvents(input, Z_SCORE_BOUNDARY)
                    .forEach(out::collect);
        }
    }
    private static List<String> generateDevices() {
        return IntStream.range(0, DEVICE_COUNT)
                .mapToObj(id -> "Device ID - " + id)
                .collect(Collectors.toList());
    }
}
