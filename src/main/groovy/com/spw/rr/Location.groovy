package com.spw.rr

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class Location {
    String locId
    String locLocId
    String locName
    String trackType
    String locLength
}
