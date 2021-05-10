package com.testing.aestroidneostats.pojo;

public class GraphPojo {

    String date;
    int noOfAstroid;


    public GraphPojo(String date, int noOfAstroid) {
        this.date = date;
        this.noOfAstroid = noOfAstroid;

    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setNoOfAstroid(int noOfAstroid) {
        this.noOfAstroid = noOfAstroid;
    }

    public String getDate() {
        return date;
    }

    public int getNoOfAstroid() {
        return noOfAstroid;
    }
}
