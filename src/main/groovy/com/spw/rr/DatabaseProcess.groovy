package com.spw.rr

import com.spw.mappers.MapperInterface
import com.spw.mappers.RunId
import com.spw.mappers.RunLoc
import com.spw.mappers.SequenceValue
import com.spw.utility.ApplyResources
import com.spw.utility.Message
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.apache.ibatis.io.Resources
import org.h2.Driver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.plaf.nimbus.State
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement
import java.util.regex.Pattern

@Singleton
class DatabaseProcess extends AbstractDatabase {

    private static final Logger log = LoggerFactory.getLogger(DatabaseProcess.class)
    private static final String SCHEMA_TEST = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"
    private static final String TABLE_TEST = "SELECT COUNT(*) from information_schema.TABLES where table_schema = ?"
    private static final String CREATE_SCHEMA = "CREATE SCHEMA "
    private static final String SET_SCHEMA = "SET SCHEMA = "
    private static final String RESOURCE_NAME = "table_create.sql"

    Integer currentSequence

    private boolean createTables(Connection conn, String schema) {
        log.debug("creating the tables")
        Statement stmt = conn.createStatement()
        stmt.execute(SET_SCHEMA + schema.toString())
        ApplyResources resources = new ApplyResources()
        return resources.excuteSQLResource(conn, RESOURCE_NAME)
    }

    public void testRoutine(String testValue) {
        log.debug("in the test routine with value ${testValue}")
    }

    String userid
    String pw
    String schema
    String url
    Message mess


    void test3() {
        log.debug("in test3")
        boolean retVal
        retVal = testRet(mess)
    }


    boolean testRet(Message newMess) {
        log.debug("in the TestRet")
        boolean retVal = vl(userid, pw, schema, url, newMess)
        log.debug("vl returned ${retVal}")
    }

    /**
     * Returns TRUE is the fields represent a valid database connection
     * @param userid
     * @param pw
     * @param url
     * @param schema
     * @return true if the fields result in a valid database connection
     */
    boolean validateFieslds(String userid, String pw, String url, String schema, Message returnMessage) {
        log.debug("now in the validator")
        boolean returnValue = false // return false if there are any issues
        Connection conn = null
        log.debug("validating parameters ${userid}, ${url}, ${schema}")
        try {
            log.debug("testing the database connection")
            conn = DriverManager.getConnection(url, userid, pw)
            if (conn != null) {
                log.trace("got a good connection with that URL, userid and password")
                PreparedStatement schemaStatement = conn.prepareStatement(SCHEMA_TEST)
                schemaStatement.setString(1, schema.toUpperCase())
                ResultSet schemaResult = schemaStatement.executeQuery()
                if (!schemaResult.next()) {
                    log.error("Should have returned a count of number of matching schemas - 0 or 1, got no result set")
                } else {
                    int matchCount = schemaResult.getInt(1)
                    int tableCount = 0
                    if (matchCount == 0) {
                        log.trace("no matching schema - creating")
                        PreparedStatement stmt = conn.prepareStatement(CREATE_SCHEMA + schema.toString())
                        stmt.execute()
                    } else if (matchCount == 1) {
                        log.trace("schema already present - checking for tables")
                        PreparedStatement stmt = conn.prepareStatement(TABLE_TEST)
                        stmt.setString(1, schema)
                        ResultSet rs = stmt.executeQuery()
                        if (!rs.next()) {
                            log.error("no result set returned on getting table count")
                            throw new RuntimeException("no result set from select count(*) for table count")
                        }
                        tableCount = rs.getInt(1)  // get count of tables in this schema
                    }
                    if (tableCount == 0) {
                        returnValue = createTables(conn, schema)
                        log.debug("create tables returned ${returnValue}")
                    } else if (tableCount != 5) {
                        log.error("got an incorrect table count - value was ${tableCount}")
                        throw new RuntimeException("Incorrect table count in schema ${schema} - count is ${tableCount}")
                    } else {
                        log.debug("all looks good - validated!")
                        returnValue = true
                    }

                }
            }
        } catch (Exception e) {
            log.error("caught an exception validating fields", e)
            returnMessage.setText("Error validating fields", Message.Level.ERROR)
        } finally {
            log.debug("closing the connection (if any)")
            if (conn != null) {
                conn.close()
            }
        }
        log.debug("validator complete - return value is ${returnValue}")
        return returnValue
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
        } else if (runIdList.size() > 1) {
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
