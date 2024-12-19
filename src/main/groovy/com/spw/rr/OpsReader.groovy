package com.spw.rr


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.xml.slurpersupport.GPathResult

class OpsReader {
    private final static Logger log = LoggerFactory.getLogger(OpsReader)

    private final DatabaseProcess db = DatabaseProcess.getInstance()

    void processFiles(String dataHome, String carFileName, String locationsFileName ) {
        log.debug("setting up to process - home is ${dataHome}, cars = ${carFileName} and locations = ${locationsFileName}")
        ParseFile parsedlocations = new ParseFile(dataHome, locationsFileName)
        ParseFile parsedCars = new ParseFile(dataHome, carFileName)
        DoLocations locs = new DoLocations(parsedlocations.getParsed())
        DoCars cars = new DoCars(parsedCars.getParsed())
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

    void staticSetup() {
        processFiles(VarData.dataHome, VarData.carFile, VarData.locationsFile)
    }

    static void main(String[] args) {
        log.debug("starting to process the Ops Data")
        OpsReader theReader = new OpsReader()
        theReader.staticSetup()
    }
}