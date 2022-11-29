package utils;

public class ClimateRecord {
    public int month;
    public double dayTemperature;
    public double nightTemperature;
    public double humidity;

    public ClimateRecord(int month, double dayTemperature, double nightTemperature, double humidity){
        this.month = month;
        this.dayTemperature = dayTemperature;
        this.nightTemperature = nightTemperature;
        this.humidity = humidity;
    }
}
