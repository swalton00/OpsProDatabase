package com.spw.rr

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class Location {

    Integer id
    String runId
    String  xmlId
    String  name
    Integer trkCount
}
