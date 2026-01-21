package com.spw.rr

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class Car {
    String carId
    String roadName
    String roadNumber
    String carType
    String carLocId
    String carSecLocId
    String carLoad
    String runId
    int carTypeId
}
