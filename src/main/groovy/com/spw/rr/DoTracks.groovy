package com.spw.rr

import groovy.xml.slurpersupport.GPathResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DoTracks {
    private static final Logger log = LoggerFactory.getLogger(DoTracks.class)

    Hashtable<String, Track> locValues = new Hashtable<>()

    DoTracks(GPathResult current) {
        log.debug("processing the locations")
        def locations = current
        int locCount = current.locations.location.size()
        log.debug("size of location list is ${locCount}")
        for (i in 0..<locCount) {
            int trackCount = current.locations.location[i].track.size()
            log.debug(" there are ${trackCount} tracks at location number ${i}")
            for (j in 0..<trackCount) {
                Track thisLoc = new Track()
                thisLoc.locId = current.locations.location[i].track[j].'@id'.text()
                thisLoc.locLocId = current.locations.location[i].'@id'.text()
                thisLoc.locName = current.locations.location[i].track[j].'@name'.text()
                thisLoc.trackType = current.locations.location[i].track[j].'@trackType'.text()
                thisLoc.locLength = current.locations.location[i].track[j].'@length'.text()
                locValues.put(thisLoc.locId, thisLoc)
            }
        }
        log.debug("location processing complete with track count =  ${locValues.size()}")
    }

    Hashtable<String, Track> getLocations() {
        return locValues
    }
}
