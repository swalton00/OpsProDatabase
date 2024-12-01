package com.spw.rr


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.xml.slurpersupport.GPathResult

class OpsReader {
    private final static Logger log = LoggerFactory.getLogger(OpsReader)

    static void main(String[] args) {
        log.debug("starting to process the Ops Data")
        GPathResult parsedLocation = new ParseFile(VarData.dataHome, VarData.locationsFile)
        log.debug("result of parse was ${parsedLocation}")
        log.error("Run complete")


    }

}