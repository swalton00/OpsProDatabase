package com.spw.view

class ViewElement {

    Integer id
    String location
    String trackName
    String trkName
    String trkId
    String carId
    String carType
    String load
    String runId
    Integer sequenceNumber
    List<InnerCar> carList

    StringBuffer sb = new StringBuffer()
    public String toString() {
        sb.setLength(0)
        if (thisRun == RunType.CAR) {
            // cars show either the Location name + track name or just the track name
            if (includeBoth) {
                sb.append(location)
                sb.append("\n")
            }
            sb.append(trackName)
            if (includeLoad) {
                sb.append("-")
                sb.append(load)
            }
        } else {
            // locations use show list of cars
            if (carList != null) {
                boolean first = true
                carList.each {
                    if (!first) {
                        first = false
                    } else {
                        sb.append("\n")
                    }
                    sb.append(it.carId)
                    if (includeLoad) {
                        if (it.load != null) {
                            sb.append("-")
                            sb.append(it.load)
                        }
                    }
                }
            }
        }

        return sb.toString()
    }

    public enum RunType {
        CAR, TRACK
    }

    static RunType thisRun = RunType.CAR
    static boolean includeLoad = true
    static boolean includeBoth = true

    public static void setType(RunType runType, boolean bothFields, boolean loads) {
        thisRun = runType
        includeBoth = bothFields
        includeLoad = loads
    }

}
