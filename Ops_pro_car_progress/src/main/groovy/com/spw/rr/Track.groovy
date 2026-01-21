package com.spw.rr

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class Track {
    Integer id
    String trkId
    String parentXmlId
    Integer parentId
    String locLocId
    String trkName
    String trackType
    String trkLength
    String runId
}
