package com.spw.rr


import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OpsReader {
    private final static Logger log = LoggerFactory.getLogger(OpsReader)

    private final DatabaseProcess db = DatabaseProcess.getInstance()
    static final String CAR_FILE = 'OperationsCarRoster.xml'
    static final String LOCATIONS_FILE = 'OperationsLocationRoster.xml'


    void processFiles(String dataHome, String dbUrl, String dbUserid, dbPw ) {
        log.debug("setting up to process - home is ${dataHome} and URL is ${dbUrl} with userid ${dbUserid}")
        ParseFile parsedlocations = new ParseFile(dataHome, LOCATIONS_FILE)
        ParseFile parsedCars = new ParseFile(dataHome, CAR_FILE)
        DoTracks locs = new DoTracks(parsedlocations.getParsed())
        DoCars cars = new DoCars(parsedCars.getParsed())
        try {
            db.initialize("com/spw/mappers/MapperInterface.xml", dbUrl, dbUserid, dbPw)
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
        processFiles(VarData.dataHome, VarData.dbUrl, VarData.dbUserid, VarData.dbPw)
    }

    static void main(String[] args) {
        log.debug("starting to process the Ops Data")
        OpsReader theReader = new OpsReader()
        theReader.staticSetup()
    }
}