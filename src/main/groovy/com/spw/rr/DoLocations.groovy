package com.spw.rr

import groovy.xml.slurpersupport.GPathResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DoLocations {
    private static final Logger log = LoggerFactory.getLogger(DoLocations.class)

    Hashtable<String, Location> locValues = new Hashtable<>()

    DoLocations(GPathResult current) {
        log.debug("processing the locations")
        def locations = current
        int locCount = current.locations.location.size()
        log.debug("size of location list is ${locCount}")
        for (i in 0..<locCount) {
            int trackCount = current.locations.location[i].track.size()
            println(" there are ${trackCount} tracks at location number ${i}")
            for (j in 0..<trackCount) {
                Location thisLoc = new Location()
                thisLoc.locId = current.locations.location[i].track[j].'@id'.text()
                thisLoc.locLocId = current.locations.location[i].'@id'.text()
                thisLoc.locName = current.locations.location[i].track[j].'@name'.text()
                thisLoc.trackType = current.locations.location[i].track[j].'@locType'.text()
                thisLoc.locLength = current.locations.location[i].track[j].'@length'.text()
                locValues.put(thisLoc.locId, thisLoc)
            }
        }
        log.debug("location processing complete with values ${locValues}")
    }

    Hashtable<String, Location> getLocations() {
        return locValues
    }
}
