package com.spw.rr

import com.spw.mappers.MapperInterface
import com.spw.mappers.RunId
import com.spw.mappers.RunLoc
import com.spw.mappers.SequenceValue
import com.spw.utility.ApplyResources
import com.spw.utility.Message

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

@Singleton
class DatabaseProcess extends AbstractDatabase {

    private static final Logger log = LoggerFactory.getLogger(DatabaseProcess.class)
    private static final String SCHEMA_TEST = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?"
    private static final String TABLE_TEST = "SELECT COUNT(*) from information_schema.TABLES where table_schema = ?"
    private static final String CREATE_SCHEMA = "CREATE SCHEMA "
    private static final String SET_SCHEMA = "SET SCHEMA = "
    private static final String GET_SEQ_MAX = "SELECT COALESCE(MAX(seq_num),0) FROM run_locs WHERE runid = ?"
    private static final String GET_SEQ_COUNT = "SELECT COUNT(*) FROM (SELECT DISTINCT seq_num FROM run_locs WHERE runid = ?) AS a"
    private static final String RESOURCE_NAME = "table_create.sql"

    Integer currentSequence

    private boolean createTables(Connection conn, String schema) {
        log.debug("creating the tables")
        Statement stmt = conn.createStatement()
        stmt.execute(SET_SCHEMA + schema.toString())
        ApplyResources resources = new ApplyResources()
        return resources.excuteSQLResource(conn, RESOURCE_NAME)
    }

    private int runWithConnection(String userid, String password, String url, String schema, String runid, String sql) {
        Connection conn = null
        int foundResult = 0
        try {
            conn = DriverManager.getConnection(url + ";SCHEMA=" + schema, userid, password)
            PreparedStatement prep = conn.prepareStatement(sql)
            prep.setString(1, runid)
            ResultSet rs = prep.executeQuery()
            if (!rs.next()) {
                log.error("get sql did not return a result set")
            } else {
                foundResult = rs.getInt(1)
            }
        } catch (Exception e) {
            log.error("Exception running the SQL statement", e)
        } finally {
            if (conn != null) {
                log.debug("closing the connection")
                conn.close()
            }
        }
        log.trace("result found was ${foundResult}")
        return foundResult
    }

    /**
     * Returns the next sequnce number or zero if none
     * @param userid
     * @param password
     * @param url
     * @param schemas
     * @param runid
     * @return
     */
    public int getSequence(String userid, String password, String url, String schema, String runid) {
        log.debug("getting the next sequence number by direct read")
        runWithConnection(userid, password, url, schema, runid, GET_SEQ_MAX)
    }

    public int getSequnceCount(String userid, String password, String url, String schema, String runid) {
        log.debug("getting the count of sequnces for this runId")
        runWithConnection(userid, password, url, schema, runid, GET_SEQ_COUNT)
    }


    String userid
    String pw
    String schema
    String url
    Message mess

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
            if (!url.startsWith("jdbc:h2:")) {
                return false
            }
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
                        log.trace("schema already present - checking for tables with schema ${schema} and sql ${TABLE_TEST}")
                        PreparedStatement stmt = conn.prepareStatement(TABLE_TEST)
                        stmt.setString(1, schema.toUpperCase())
                        ResultSet rs = stmt.executeQuery()
                        if (!rs.next()) {
                            log.error("no result set returned on getting table count")
                            throw new RuntimeException("no result set from select count(*) for table count")
                        }
                        tableCount = rs.getInt(1)  // get count of tables in this schema
                        log.trace("table count was ${tableCount}")
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
            returnValue = false
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
        RunId thisRunId = map.selectRunId(runId)
        Integer nextSequence = map.getSequenceMax(runId) + 1
        currentSequence = nextSequence
        if (thisRunId == null) {
            log.debug("don't have a current sequnce -- setting it to ${nextSequence}")
            RunId newRunId = new RunId()
            newRunId.runid = runId
            newRunId.comment = runComment
            newRunId.sequenceNumber = nextSequence
            map.insertRunId(newRunId)
        } else {
            log.debug("updating sequence to new value of ${nextSequence}")
            thisRunId.comment = runComment
            thisRunId.sequenceNumber = nextSequence
            map.updateRunId(thisRunId)
        }
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

    Location findLocation(Location location) {
        log.debug("finding or creating the Location record")
        MapperInterface map = session.getMapper(MapperInterface.class)
        Location locValue = map.getLocation(location)
        if (locValue == null) {
            log.debug("Location was not found - inserting")
            map.insertLocation(location)
            locValue = location
            log.debug("resulting location was ${location}")
        }
        return locValue
    }

    void mergeTrack(Track thisTrk) {
        log.debug("inserting or updating this location ${thisTrk}")

        MapperInterface map = session.getMapper(MapperInterface.class)
        Track original = map.getTrack(thisTrk)
        if (original == null) {
            log.debug("track was not found - adding it - ${thisTrk}")
            map.insertTrack(thisTrk)
        } else {
            thisTrk.id = original.id
            log.debug("updating track to be ${thisTrk}")
            map.updateTrack(thisTrk)
        }
    }

}
