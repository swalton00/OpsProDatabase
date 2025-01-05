package com.spw.view

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

    public List<ViewLoc> listViewLocs(String runId) {
        ViewInterface map = session.getMapper(ViewInterface.class)
        List<ViewLoc> locs = map.listViewLocs(runId)
        log.debug("got a list of ${locs.size()}")
        return locs
    }

    public List<Integer> getSequences(String runId) {
        ViewInterface map = session.getMapper(ViewInterface.class)
        List<Integer> results = map.getSequenceNumbers(runId)
        log.debug("returning a list of sequnce numbers with ${results.size()} entries on it")
        return results
    }

    public List<RowElement> getRowEleents(ViewParameter parameters) {
        ViewInterface map = session.getMapper(ViewInterface.class)
        List<RowElement> results = map.listRows(parameters)
        log.debug("got a result set with ${results} rows")
        return results
    }

    public List<RowElement> getRowLocs(ViewParameter parameter) {
        ViewInterface map = session.getMapper(ViewInterface.class)
        List<RowElement> results = map.listLocRows(parameter)
        log.debug("the resutts were ${results.size()}")
        return results
    }
}
