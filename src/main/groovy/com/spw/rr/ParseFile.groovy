package com.spw.rr

import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import groovy.xml.DOMBuilder
import org.w3c.dom.Document

@ToString(includePackage = false, includeNames = true)
class ParseFile {
    private static final Logger log = LoggerFactory.getLogger(ParseFile.class)

    Document parsedText

    public ParseFile(String fileLocation, String fileName) {
        log.debug("creating a ParseFile with ${fileLocation} and ${fileName}")
        File fileLoc = new File(fileLocation)
        File inputFile = new File(fileLoc, fileName)
        if (!inputFile.exists()) {
            log.error("Input file ${fileName} does not exist at location ${fileLoc}")
            System.exit(12)
        }
        BufferedReader rdr = new BufferedReader(new FileReader((inputFile)))
        parsedText = DOMBuilder.parse(rdr, false, true, true)
        log.debug("processing xml to get locations ${parsedText}")
    }

    Document getParsed() {
        log.debug("returning the parsed XML")
        return parsedText
    }


}
