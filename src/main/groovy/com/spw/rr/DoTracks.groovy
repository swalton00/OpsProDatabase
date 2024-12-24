package com.spw.rr

import groovy.xml.slurpersupport.GPathResult
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class DoTracks {
    private static final Logger log = LoggerFactory.getLogger(DoTracks.class)

    Hashtable<String, Location> locValues = new Hashtable<>()
    Hashtable<String, Track> tracks = new Hashtable<>()

    DoTracks(GPathResult current) {
        log.debug("processing the locations")
        def locations = current
        int locCount = current.locations.location.size()
        log.debug("size of location list is ${locCount}")
        for (i in 0..<locCount) {
            Location location = new Location()
            location.name = current.locations.location[i].'@name'.text()
            location.xmlId = current.locations.location[i].'@id'.text()
            int trackCount = current.locations.location[i].track.size()
            location.trkCount = trackCount
            locValues.put(location.xmlId, location)
            log.debug(" there are ${trackCount} tracks at location number ${i}")
            for (j in 0..<trackCount) {
                Track thisLoc = new Track()
                thisLoc.parentXmlId = location.xmlId
                thisLoc.trkId = current.locations.location[i].track[j].'@id'.text()
                thisLoc.locLocId = current.locations.location[i].'@id'.text()
                thisLoc.trkName = current.locations.location[i].track[j].'@name'.text()
                thisLoc.trackType = current.locations.location[i].track[j].'@trackType'.text()
                thisLoc.trkLength = current.locations.location[i].track[j].'@length'.text()
                tracks.put(thisLoc.trkId, thisLoc)
            }
        }
        log.debug("location processing complete with track count =  ${locValues.size()}")
    }

    Hashtable<String, Location> getLocations() {
        return locValues
    }

    Hashtable<String, Track> getTracks() {
        return tracks
    }
}
