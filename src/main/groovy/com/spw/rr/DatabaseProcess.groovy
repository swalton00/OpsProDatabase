package com.spw.rr

import com.spw.mappers.MapperInterface
import com.spw.mappers.RunId
import com.spw.mappers.SequenceValue
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.io.Resources
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Singleton
class DatabaseProcess {

    static DatabaseProcess db = null
    private static final Logger log = LoggerFactory.getLogger(DatabaseProcess.class)

    SqlSessionFactory sqlSessionFactory = null
    SqlSession session = null

    void intialize(String url, String userid, String password) {
        String resource = "mybatis-config.xml";
        Properties props = new Properties()
        props.put("url", url)
        props.put("username", userid)
        props.put("password", password)
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, props );
        session = sqlSessionFactory.openSession(org.apache.ibatis.session.ExecutorType.REUSE, true)
    }

    void setRunId(String runId, String runComment) {
        log.debug("setting runid to ${runId}")
        MapperInterface map = session.getMapper(MapperInterface.class)
        RunId newRunId = new RunId()
        newRunId.runid = runId
        newRunId.comment = runComment
        List<RunId> runIdList = map.selectRunId(newRunId)
        SequenceValue seq = new SequenceValue()
        seq.runId = runId
        seq.currentSeq = 1
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
            int seqCount = map.getSequenceCount(newRunId.runid)
            if (seqCount == 0) {
                map.insertSequence(seq)
            } else if (seqCount == 1) {
                map.updateSequence(seq)
            } else {
                throw new RunId("more than one sequence count for this runid ${runId}")
            }
        }
        else if (runIdList.size() > 1) {
            log.error("runId List has more than one item -- there are ${runIdList.size()} items in the list deleting all")
        }
        log.debug("succesful completion of the setup for this runid/sequnce - ${runId}, ${seq.currentSeq}")
    }

    void endRun() {
        log.debug("ending the run -- closing the connection")
        session.close()
    }
}
