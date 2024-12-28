package com.spw.mappers

import com.spw.rr.AbstractDatabase
import com.spw.view.ViewCar
import com.spw.view.ViewInterface
import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ToString(includeFields = true, includePackage = false, includeNames = true)
@Singleton
class ViewDatabase extends AbstractDatabase {

    private static final Logger log = LoggerFactory.getLogger(ViewDatabase.class)

    public List<ViewCar> listCars(String runId) {
        ViewInterface map = session.getMapper(ViewInterface.class)
        List<ViewCar> carList = map.listViewCars(runId)
        log.debug("got a list of ${carList.size()}")
        return carList
    }
}
