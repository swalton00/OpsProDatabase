package com.spw.rr


import com.spw.mappers.RunLoc
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ProcessData {
    private static final Logger log = LoggerFactory.getLogger(ProcessData.class)

    public static void doCars(Hashtable<String, Car> carList) {
        log.debug("processing a car hashtable of size ${carList.size()}")
        DatabaseProcess db = DatabaseProcess.getInstance()
        int currentSeq = db.getCurrentSequence()
        String currentRun = VarData.runId
        carList.values().forEach {thisCar ->
            thisCar.runId = currentRun
            db.mergeCar(thisCar)
            RunLoc runLoc = new RunLoc()
            runLoc.runId = currentRun
            runLoc.carId = thisCar.carId
            runLoc.seqNum = currentSeq
            runLoc.trkId = thisCar.carSecLocId
            runLoc.load = thisCar.carLoad
            db.insertRunLoc(runLoc)
        }
    }

    public static void doLocations(Hashtable<String, Location> locList, Hashtable<String, Track> tracks) {
        log.debug("processing a locations list of ${locList.size()} entries with ${tracks.size} tracks")
        String currentRun = VarData.runId
        DatabaseProcess db = DatabaseProcess.getInstance()
        Set<String> keyList = locList.keySet()
        keyList.each {
            log.debug("processing location key of ${it} with a value of ${locList.get(it)}")
            Location thisLoc = locList.get(it)
            thisLoc.runId = currentRun
            thisLoc = db.findLocation(thisLoc)
            locList.put(it, thisLoc)
            log.debug("processed location key of ${it} with a value of ${locList.get(it)}")
        }
        tracks.values().forEach { thisTrk ->
            //log.debug("Processing track ${thisTrk}")
            thisTrk.runId = currentRun
            thisTrk.parentId = locList.get(thisTrk.parentXmlId).id
            db.mergeTrack(thisTrk)
        }
    }

}
