package org.task;


import java.util.Objects;

public class TemperatureEvent {
    public String deviceId;
    public double temperature;
    public long timestamp;

    public TemperatureEvent() {
    }

    public TemperatureEvent(String deviceId, double temperature, long timestamp) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemperatureEvent that = (TemperatureEvent) o;
        return Double.compare(that.temperature, temperature) == 0 && timestamp == that.timestamp && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, temperature, timestamp);
    }

    @Override
    public String toString() {
        return "DeviceTemperatureEvent{" +
                "deviceId='" + deviceId + '\'' +
                ", temperature=" + temperature +
                ", timestamp=" + timestamp +
                '}';
    }


}
