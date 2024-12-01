package com.spw.rr

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includeFields = true)
class Location {
    String locId
    String locName
    String trackType
    String locType
    String locLength
}
