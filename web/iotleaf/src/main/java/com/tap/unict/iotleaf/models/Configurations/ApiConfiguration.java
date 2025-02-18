package com.tap.unict.iotleaf.models.Configurations;

public class ApiConfiguration{
    private String name;
    private Double min;
    private Double max;
    private String format;

    public ApiConfiguration(){}
    public ApiConfiguration(String name, Double min, Double max, String format) {
        this.name = name;
        this.min = min;
        this.max = max;
        this.format = format;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Double getMin() {
        return min;
    }
    public void setMin(Double min) {
        this.min = min;
    }
    public Double getMax() {
        return max;
    }
    public void setMax(Double max) {
        this.max = max;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    
}