package com.spw.mappers

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class SequenceValue {
    int id
    String runId
    int currentSeq
}
