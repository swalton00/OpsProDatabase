package com.spw.rr

import com.spw.mappers.CarType
import groovy.xml.slurpersupport.GPathResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DoCars {

    private static final Logger log = LoggerFactory.getLogger(DoCars.class)
    Hashtable<String, Car> carValues = new Hashtable<>()
    DatabaseProcess db = DatabaseProcess.getInstance()

    DoCars(GPathResult carXml, String runId) {
        log.debug("processing cars")
        int typeCount = carXml.types.type.size()
        log.debug("There are ${typeCount} types in the XML")
        Hashtable<String, CarType> typeHash = new Hashtable<>()
        List<CarType> carTypeList = db.listCarTypes(runId)
        log.debug("got back a list of ${carTypeList.size()} carTypes")
        carTypeList.forEach {
            it.runId = runId
            typeHash.put(it.carType, it)
        }
        for (i in 0..<typeCount) {
            CarType ct = new CarType()
            String thisType = carXml.types.type[i].'@name'.text()
            log.debug("found a type ${thisType} at entry ${i}")
            CarType found = typeHash.get(thisType)
            if (found == null) {
                log.debug("type ${thisType} wasn't found -- adding")
                CarType newCt = new CarType()
                newCt.runId = runId
                newCt.carType = thisType
                newCt = db.insertCarType(newCt)
                log.debug("inserted CarType result is ${newCt}")
                typeHash.put(thisType, newCt)
            }
        }
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
            thisCar.carDestLocId = carXml.cars.car[i].'@secDestinationId'.text()
            thisCar.carLoad = carXml.cars.car[i].'@load'.text()
            String tempType = carXml.cars.car[i].'@type'.text()
            log.debug("this car type is ${tempType}")
            CarType tempCt = typeHash.get(tempType)
            if (tempType == null) {
                log.error("type ${tempType} wasn't found")
            } else {
                log.debug("found a type id of ${tempCt}")
                thisCar.carTypeId = tempCt.id
                log.debug("this car is now ${thisCar}")
            }
            if (thisCar.carSecLocId.isEmpty() & !thisCar.carLocId.isEmpty()) {
                log.debug("car ${thisCar.roadName} - ${thisCar.roadNumber} doesn't have a current full location - using destination")
                if (!thisCar.carDestLocId.isEmpty()) {
                    thisCar.carSecLocId = thisCar.carDestLocId
                }
            }

            if (thisCar.carLocId != null & !thisCar.carLocId.isEmpty()) {
                carValues.put(thisCar.carId, thisCar)
                log.debug("added car ${thisCar}")
            } else [
                    log.debug(("skipping car ${thisCar.carId} because the location is empty"))
            ]
        }
        log.debug("${carValues.size()} cars were added")
        log.debug("cars hash is ${carValues}")
    }

    Hashtable<String, Car> getCarList() {
        return carValues
    }
}
