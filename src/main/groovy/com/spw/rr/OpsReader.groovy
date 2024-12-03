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
        db.intialize(VarData.dbUrl, VarData.dbUserid, VarData.dbPw)
        log.info("Run complete")


    }

}