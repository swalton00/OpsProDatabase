package com.spw.rr

import com.spw.mappers.RunId
import com.spw.mappers.RunIdent
import com.spw.ui.MainModel
import com.spw.utility.PropertySaver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class OpsReader {
    private final static Logger log = LoggerFactory.getLogger(OpsReader)
    private static final PropertySaver saver = PropertySaver.getInstance()
    private static final DatabaseProcess db = DatabaseProcess.getInstance()
    static final String CAR_FILE = 'OperationsCarRoster.xml'
    static final String LOCATIONS_FILE = 'OperationsLocationRoster.xml'


    void processFiles(String dataHome, String runId, String runComment) {
        log.debug("setting up to process - home is ${dataHome}")
        ParseFile parsedlocations = new ParseFile(dataHome, LOCATIONS_FILE)
        ParseFile parsedCars = new ParseFile(dataHome, CAR_FILE)
        DoTracks locs = new DoTracks(parsedlocations.getParsed())
        DoCars cars = new DoCars(parsedCars.getParsed(), runId)
        try {
            Integer currentSequence = db.getCurrentSequence()
            RunIdent runIdent = new RunIdent()
            runIdent.runComment = runComment
            runIdent.runId = runId
            runIdent.seqNo = db.getCurrentSequence()
            db.insertRunIdent(runIdent)
            /* need to do locations first since runLocs will reference Locations */
            ProcessData.doLocations(locs.getLocations(), locs.getTracks(), runId)
            ProcessData.doCars(cars.getCarList(), runId)
        } catch (Exception e) {
            log.error("exception in processing", e)
        }
        log.info("Run complete")
    }

    boolean checkfield(String field) {
        if (field == null) {
            return true
        }
        if (field.isBlank()) {
            return true
        }
        return false
    }

    void staticSetup() {
        saver.init()
        String url = saver.getBaseString("url")
        String userid = saver.getBaseString("userid")
        String pw = saver.getBaseString("password")
        String schema = saver.getBaseString("schema")
        String opsHome = saver.getBaseString("opsHome")
        String runId = saver.getBaseString("runId")
        String runComment = saver.getBaseString("runComment")
        boolean invalidField = checkfield(url)
        invalidField |= checkfield(userid)
        invalidField |= checkfield(pw)
        invalidField |= checkfield(schema)
        invalidField |= checkfield(opsHome)
        invalidField |= checkfield(runId)
        if (runComment == null) {
            runComment = ""
        }
        if (invalidField) {
            throw new RuntimeException("command line version invoked without saved properties")
        }
        db.initialize(url, schema, userid, pw)
        db.setRunId(runId, runComment)
        processFiles(opsHome, runId)
    }

    static void main(String[] args) {
        log.debug("starting to process the Ops Data")
        OpsReader theReader = new OpsReader()
        theReader.staticSetup()
        db.endRun()
    }
}