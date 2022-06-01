package org.task;

import org.apache.flink.shaded.guava30.com.google.common.math.Stats;

import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public final class OutlierDetectionUtil {

    private OutlierDetectionUtil() {
    }

    /**
     *
     * @param events list of temperature events
     * @param zScoreBoundary - A Z-score is a numerical measurement that describes
     *                       a value's relationship to the mean of a group of values.
     *                       A Z-score of 1.0 would indicate a value that is one standard deviation from the mean
     * @return list of temperature events where a measured temperature is outside of @param zScoreBoundary
     */

    public static List<TemperatureEvent> detectOutlierTemperatureEvents(Iterable<TemperatureEvent> events, double zScoreBoundary) {
        List<Double> samples = StreamSupport.stream(events.spliterator(), false)
                .map(event -> event.temperature)
                .collect(toList());
        Stats stats = Stats.of(samples);
        return StreamSupport.stream(events.spliterator(), false)
                .filter(event -> calculateZScore(event.temperature, stats) > zScoreBoundary)
                .collect(toList());
    }

    private static double calculateZScore(double sample, Stats stats) {
        return Math.abs((sample - stats.mean()) / stats.sampleStandardDeviation());
    }

}
