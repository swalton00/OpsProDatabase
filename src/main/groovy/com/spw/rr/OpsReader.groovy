package com.spw.rr


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.xml.slurpersupport.GPathResult

class OpsReader {
    private final static Logger log = LoggerFactory.getLogger(OpsReader)

    static DatabaseProcess db = null

    static void main(String[] args) {
        log.debug("starting to process the Ops Data")
        ParseFile parsedLocation = new ParseFile(VarData.dataHome, VarData.locationsFile)
        log.debug("result of parse was ${parsedLocation.getParsed()}")
        DoLocations locs = new DoLocations(parsedLocation.getParsed())
        ParseFile parsedCars = new ParseFile(VarData.dataHome, VarData.carFile)
        DoCars cars = new DoCars(parsedCars.getParsed())
        db = DatabaseProcess.getInstance()
        try {
            db.initialize("com/spw/mappers/MapperInterface.xml", VarData.dbUrl, VarData.dbUserid, VarData.dbPw)
            db.setRunId(VarData.runId, VarData.runComment)
            Integer currentSequence = db.getCurrentSequence()
            /* need to do locations first since runLocs will reference Locations */
            ProcessData.doLocations(locs.getLocations())
            ProcessData.doCars(cars.getCarList())

        } catch (Exception e) {
            log.error("exception in processing", e)
        } finally {
            db.endRun()
        }
        log.info("Run complete")
    }
}