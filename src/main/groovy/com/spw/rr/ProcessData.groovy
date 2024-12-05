package com.spw.rr

import com.spw.mappers.MapperInterface
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
            runLoc.locId = thisCar.carSecLocId
            runLoc.load = thisCar.carLoad
            db.insertRunLoc(runLoc)
        }
    }

    public static void doLocations(Hashtable<String, Location> locList) {
        log.debug("processing a locations list of ${locList.size()} entries")
        String currentRun = VarData.runId
        DatabaseProcess db = DatabaseProcess.getInstance()
        int currentSeq = db.getCurrentSequence()
        locList.values().forEach { thisLoc ->
            thisLoc.runId = currentRun
            db.mergeLocation(thisLoc)
        }
    }

}
