package com.spw.rr


import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OpsReader {
    private final static Logger log = LoggerFactory.getLogger(OpsReader)

    private static final DatabaseProcess db = DatabaseProcess.getInstance()
    static final String CAR_FILE = 'OperationsCarRoster.xml'
    static final String LOCATIONS_FILE = 'OperationsLocationRoster.xml'


    void processFiles(String dataHome) {
        log.debug("setting up to process - home is ${dataHome}")
        ParseFile parsedlocations = new ParseFile(dataHome, LOCATIONS_FILE)
        ParseFile parsedCars = new ParseFile(dataHome, CAR_FILE)
        DoTracks locs = new DoTracks(parsedlocations.getParsed())
        DoCars cars = new DoCars(parsedCars.getParsed())
        try {
            Integer currentSequence = db.getCurrentSequence()
            /* need to do locations first since runLocs will reference Locations */
            ProcessData.doLocations(locs.getLocations(), locs.getTracks())
            ProcessData.doCars(cars.getCarList())
        } catch (Exception e) {
            log.error("exception in processing", e)
        }
        log.info("Run complete")
    }



    void staticSetup() {
        db.initialize(VarData.dbUrl, VarData.dbUserid, VarData.dbPw)
        db.setRunId(VarData.runId, VarData.runComment)
        processFiles(VarData.dataHome)
    }

    static void main(String[] args) {
        log.debug("starting to process the Ops Data")
        OpsReader theReader = new OpsReader()
        theReader.staticSetup()
        db.endRun()
    }
}