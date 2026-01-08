package com.spw.view;

public class ViewCar {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getRoadNumber() {
        return roadNumber;
    }

    public void setRoadNumber(String roadNumber) {
        this.roadNumber = roadNumber;
    }

    public String getCarLoad() {
        return carLoad;
    }

    public void setCarLoad(String carLoad) {
        this.carLoad = carLoad;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    int id;
    String roadName;
    String roadNumber;
    String carType;
    String carLoad;
    String carId;

    public String toString() {
        return roadName + roadNumber;
    }


}
