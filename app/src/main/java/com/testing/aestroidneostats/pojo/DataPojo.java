package com.testing.aestroidneostats.pojo;

public class DataPojo {

    String id;
    double speed;
    double closestDistance;

    public DataPojo(String id, double speed, double closestDistance) {
        this.id = id;
        this.speed = speed;
        this.closestDistance = closestDistance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getClosestDistance() {
        return closestDistance;
    }

    public void setClosestDistance(double closestDistance) {
        this.closestDistance = closestDistance;
    }
}
