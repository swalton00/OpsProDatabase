package com.spw.rr

import groovy.xml.slurpersupport.GPathResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DoCars {

    private static final Logger log = LoggerFactory.getLogger(DoCars.class)
    Hashtable<String, Car> carValues = new Hashtable<>()

    DoCars(GPathResult carXml) {
        log.debug("processing cars")
        int carCount = carXml.cars.car.size()
        log.debug("there are ${carCount} cars found")
        for (i in 0..<carCount) {
            Car thisCar = new Car()
            def currentLoc = carXml.cars.car[i]
            thisCar.carId = carXml.cars.car[i].'@id'.text()
            thisCar.roadName = carXml.cars.car[i].'@roadName'.text()
            thisCar.roadNumber = carXml.cars.car[i].'@roadNumber'.text()
            thisCar.carType = carXml.cars.car[i].'@type'.text()
            thisCar.roadName = carXml.cars.car[i].'@roadName'.text()
            thisCar.carLocId = carXml.cars.car[i].'@locationId'.text()
            thisCar.roadName = carXml.cars.car[i].'@roadName'.text()
            thisCar.carSecLocId = carXml.cars.car[i].'@secLocationId'.text()
            thisCar.carLoad = carXml.cars.car[i].'@load'.text()
            if (thisCar.carLocId != null & !thisCar.carLocId.isEmpty()) {
                carValues.put(thisCar.carId, thisCar)
                log.debug("added car ${thisCar}")
            } else [
                    log.info(("skipping car ${thisCar.carId} because the location is empty"))
            ]
        }
        log.debug("${carValues.size()} cars were added")
        log.debug("cars hash is ${carValues}")
    }

    Hashtable<String, Car> getCarList() {
        return carValues
    }
}
