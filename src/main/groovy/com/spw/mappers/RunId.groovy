package com.spw.mappers

import groovy.transform.ToString

@ToString(includeFields = true, includePackage = false, includeNames = true)
class RunId {
    int id
    String runid
    String comment
    Integer sequenceNumber
}
