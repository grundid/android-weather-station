package de.grundid.weather;

public class SensorData {
    private String sensorName;
    private double value;
    private long date;

    public SensorData(String sensorName, double value, long date) {
        this.sensorName = sensorName;
        this.value = value;
        this.date = date;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getSensorName() {

        return sensorName;
    }

    public double getValue() {
        return value;
    }

    public long getDate() {
        return date;
    }
}
