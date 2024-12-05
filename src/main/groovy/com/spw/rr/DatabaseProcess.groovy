package com.spw.rr

import com.spw.mappers.MapperInterface
import com.spw.mappers.RunId
import com.spw.mappers.RunLoc
import com.spw.mappers.SequenceValue
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.io.Resources
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class DatabaseProcess extends AbstractDatabase {

    private static final Logger log = LoggerFactory.getLogger(DatabaseProcess.class)

    Integer currentSequence

    void setRunId(String runId, String runComment) {
        log.debug("setting runid to ${runId}")
        MapperInterface map = session.getMapper(MapperInterface.class)
        RunId newRunId = new RunId()
        newRunId.runid = runId
        newRunId.comment = runComment
        List<RunId> runIdList = map.selectRunId(newRunId)
        SequenceValue seq = new SequenceValue()
        seq.runId = runId
        currentSequence = 1
        seq.currentSeq = currentSequence
        /*
            if RunId list exists, update it and set RunSequence to 1
            If it doesn't, insert it and create new RunSequence at 1
         */
        if (runIdList.isEmpty()) {
            log.debug("runId list is empty - creating one")
            int insertCount = map.insertRunId(newRunId)
            log.debug("new runid is now ${newRunId} -- count was ${insertCount}")
            insertCount = map.insertSequence(seq)
            log.debug("SequenceValue is now ${seq} and count is ${insertCount}")

        } else if (runIdList.size() == 1) {
            map.updateRunId(newRunId)
            List<SequenceValue> seqList = map.getSequenceList(newRunId.runid)
            log.debug("got a sequence list of size ${seqList.size()}")
            if (seqList.size() == 0) {
                currentSequence = 1
                map.insertSequence(seq)
            } else if (seqList.size() == 1) {
                SequenceValue curSeq = seqList.get(0)
                currentSequence = curSeq.currentSeq + 1
                curSeq.currentSeq = currentSequence
                map.updateSequence(curSeq)
                log.debug("updating Seq number to ${currentSequence}")
            } else {
                throw new RunId("more than one sequence count for this runid ${runId}")
            }
        }
        else if (runIdList.size() > 1) {
            log.error("runId List has more than one item -- there are ${runIdList.size()} items in the list deleting all")
        }
        log.debug("succesful completion of the setup for this runid/sequnce - ${runId}, ${seq.currentSeq}")
    }

    int getCurrentSequence() {
        log.debug("returning current sequence number which is ${currentSequence}")
        return currentSequence
    }

    void mergeCar(Car thisCar) {
        log.debug("merging current car into database ${thisCar}")
        MapperInterface map = session.getMapper(MapperInterface.class)
        map.mergeCar(thisCar)
    }

    void insertRunLoc(RunLoc runLoc) {
        log.debug("inserting this runLoc ${runLoc}")
        MapperInterface map = session.getMapper(MapperInterface.class)
        map.insertRunLoc(runLoc)
    }

    void mergeLocation(Location thisLoc) {
        log.debug("inserting or updating this location ${thisLoc}")
        MapperInterface map = session.getMapper(MapperInterface.class)
        map.mergeLocation(thisLoc)
    }

}
