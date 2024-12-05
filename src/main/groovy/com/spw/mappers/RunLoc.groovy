package com.spw.mappers

import groovy.transform.ToString

@ToString(includeFields = true, includePackage = false, includeNames = true)
class RunLoc {
    String runId
    int     seqNum
    String  carId
    String  locId
    String  load
}
