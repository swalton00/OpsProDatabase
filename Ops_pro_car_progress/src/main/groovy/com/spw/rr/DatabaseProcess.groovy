package com.spw.rr

import com.spw.mappers.CarType
import com.spw.mappers.MapperInterface
import com.spw.mappers.RunId
import com.spw.mappers.RunIdent
import com.spw.mappers.RunLoc
import com.spw.utility.ApplyResources
import com.spw.utility.Message
import org.apache.ibatis.session.SqlSession
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
    private static final String DB_VERSION_TEST = "SELECT COUNT(*) FROM information_schema.TABLES where table_schema = ? AND TABLE_NAME = 'OPS_DB_VERSION'"
    private static final String CREATE_SCHEMA = "CREATE SCHEMA "
    private static final String SET_SCHEMA = "SET SCHEMA = "
    private static final String GET_SEQ_MAX = "SELECT COALESCE(MAX(seq_num),0) FROM run_locs WHERE runid = ?"
    private static final String GET_SEQ_COUNT = "SELECT COUNT(*) FROM (SELECT DISTINCT seq_num FROM run_locs WHERE runid = ?) AS a"
    private static final String TABLE_CREATE = "table_create.sql"
    public static final int DB_MAJOR = 1
    public static final int DB_MINOR = 1

    Integer currentSequence

    private boolean applySQL(Connection conn, String schema, String resourceName) {
        log.debug("creating the tables")
        Statement stmt = conn.createStatement()
        stmt.execute(SET_SCHEMA + schema.toString())
        ApplyResources resources = new ApplyResources()
        return resources.excuteSQLResource(conn, resourceName)
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
     * Returns the next sequence number or zero if none
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

    public int getSequenceCount(String userid, String password, String url, String schema, String runid) {
        log.debug("getting the count of sequences for this runId")
        runWithConnection(userid, password, url, schema, runid, GET_SEQ_COUNT)
    }

    /**
     * Verify that userid, password and url will work
     * @param userid the userid
     * @param pw the password
     * @param url the URL which should start with "jdbc:h2:"
     * @param message a message to be set based on success or failure
     * @return true if the connection succeeds
     */
    boolean verifyConnect(String userid, String pw, String url, Message message) {
        boolean retValue = false
        log.debug("verify that we can connect via userid ${userid} and url ${url}")
        Connection conn = null
        try {
            conn = DriverManager.getConnection(url, userid, pw)
            if (conn != null) {
                log.debug("connection succeeded")
                retValue = true
                message.setText("", Message.Level.INFO)
            }
        } catch (Exception e) {
            message.setText(e.getMessage(), Message.Level.ERROR)
            log.error("Connection failed - exception was ${e.getMessage()}", e)
        } finally {
            if (conn != null) {
                conn.close()
            }
        }
        return retValue
    }


    /**
     * Check the database version (from OP_DB_VERSION table) and upgrade if necessary
     * @param conn the connection to use
     * @param schema the verified schema (created if needed and already upper case)
     *
     * If the OPS_DB_VERSION table does not yet exist, the major and minor versions are 1 and 0
     * If no tables exist, tables are created
     * If tables exist, but major and minor version don't match, tha database is upgraded
     *      Upgrade files are name "Version_{major}_{minor} using the numbers from OPS_DB_VERSION
     *      Files are applied, starting with the current version
     *      Continuing until Major and Minor match the current
     */
    private void checkDBVersion(Connection conn, String schema) {
        log.debug("checking database version")
        PreparedStatement stmt = conn.prepareStatement(TABLE_TEST)
        stmt.setString(1, schema.toUpperCase())
        ResultSet rs = stmt.executeQuery()
        int versionCount = 0
        int tableCount = 0
        if (!rs.next()) {
            log.error("no result set returned on checking for tables")
            throw new RuntimeException("no result set from select count(*) for table count")
        }
        tableCount = rs.getInt(1) // count of tables in the schema
        if (tableCount == 0) {
            log.debug("no tables found -- creating")
            boolean   returnValue = applySQL(conn, schema, TABLE_CREATE)
            log.debug("create tables returned ${returnValue}")
            // done should have created to current tables
            return
        }

        boolean repeatLoop = true
        while (repeatLoop) {
            stmt = conn.prepareStatement(DB_VERSION_TEST)
            stmt.setString(1, schema.toUpperCase())
            rs = stmt.executeQuery()
            if (!rs.next()) {
                log.error("No rows found looking for Versions table - should always be 1 row with count")
                thrown new RuntimeException("No rows found for a select count")
            }
            versionCount = rs.getInt(1)  // get count of tables in this schema
            log.trace("db version count was  ${versionCount}")
            int foundMajor   // default values for major and minor
            int foundMinor
            if (versionCount == 0) {
                log.debug("using default values for DB major and minor")
                foundMajor = 1
                foundMinor = 0
            } else {
                stmt = conn.prepareStatement("SELECT MAJOR, MINOR from ${schema}.OPS_DB_VERSION")
                rs = stmt.executeQuery()
                if (!rs.next()) {
                    log.error("no result set for version query, table present, but no rows")
                    throw new RuntimeException("No row in version table")
                }
                foundMajor = rs.getInt(1)
                foundMinor = rs.getInt(2)
                log.debug("Version table was found - using result Major=${foundMajor}, minor = ${foundMinor}")
            }
            if (foundMajor == DB_MAJOR & foundMinor == DB_MINOR) {
                repeatLoop = false
                break
            }
            if (foundMajor > DB_MAJOR | (foundMajor == DB_MAJOR & foundMinor > DB_MINOR)) {
                log.error("Database version is greated than application version")
                System.exit(8)
            }
            String upgradeSQL = "Version_${foundMajor}_${foundMinor}.sql"
            log.debug("applying Sql found in resource ${upgradeSQL}")
            applySQL(conn, schema, upgradeSQL)
        }
    }

/**
 * Returns TRUE is the fields represent a valid database connection
 * @param url
 * @param schema
 * @param userid
 * @param password
 * @param message where a response message will be set
 * @return true if the fields result in a valid database connection
 */
        boolean validateFields(String url, String schema, String userid, String password, Message returnMessage) {
            log.debug("now in the validator")
            boolean returnValue = false // return false if there are any issues
            Connection conn = null
            log.debug("validating parameters ${userid}, ${url}, ${schema}")
            try {
                log.debug("testing the database connection")
                if (!url.startsWith("jdbc:h2:")) {
                    return false
                }
                conn = DriverManager.getConnection(url, userid, password)
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
                        }
                        checkDBVersion(conn, schema)
                    }
                }
                returnValue = true
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
            SqlSession session = null
            try {
                session = getSession()
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
            } catch (Exception e) {
                log.error("Exception working with the database", e)
            } finally {
                if (session != null) {
                    log.debug("closing the session now")
                    session.close()
                }
            }
        }

        int getCurrentSequence() {
            log.debug("returning current sequence number which is ${currentSequence}")
            return currentSequence
        }

        void mergeCar(Car thisCar) {
            log.debug("merging current car into database ${thisCar}")
            SqlSession session = null
            try {
                session = getSession()
                MapperInterface map = session.getMapper(MapperInterface.class)
                map.mergeCar(thisCar)
            } catch (Exception e) {
                log.error("Exception working with the database", e)
            } finally {
                if (session != null) {
                    log.debug("closing the session now")
                    session.close()
                }
            }
        }


        void insertRunLoc(RunLoc runLoc) {
            log.debug("inserting this runLoc ${runLoc}")
            SqlSession session = null
            try {
                session = getSession()
                MapperInterface map = session.getMapper(MapperInterface.class)
                map.insertRunLoc(runLoc)
            } catch (Exception e) {
                log.error("Exception working with the database", e)
            } finally {
                if (session != null) {
                    log.debug("closing the session now")
                    session.close()
                }
            }
        }

        Location findLocation(Location location) {
            log.debug("finding or creating the Location record")
            SqlSession session = null
            Location locValue = null
            try {
                session = getSession()
                MapperInterface map = session.getMapper(MapperInterface.class)
                locValue = map.getLocation(location)
                if (locValue == null) {
                    log.debug("Location was not found - inserting")
                    map.insertLocation(location)
                    locValue = location
                    log.debug("resulting location was ${location}")
                }
                return locValue
            } catch (Exception e) {
                log.error("Exception working with the database", e)
            } finally {
                if (session != null) {
                    log.debug("closing the session now")
                    session.close()
                }

            }
            return locValue
        }

        void mergeTrack(Track thisTrk) {
            log.debug("inserting or updating this location ${thisTrk}")
            SqlSession session = null
            try {
                session = getSession()
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
            } catch (Exception e) {
                log.error("Exception working with the database", e)
            } finally {
                log.debug("closing the session now")
                session.close()
            }
        }

        void insertRunIdent(RunIdent runIdent) {
            log.debug("writing the runIdent ${runIdent}")
            SqlSession session = null
            try {
                session = getSession()
                MapperInterface mapper = session.getMapper(MapperInterface.class)
                mapper.insertRunIdent(runIdent)
            } catch (Exception e) {
                log.error("Exception writing the runIdent", e)
            } finally {
                log.debug("closing the session")
                if (session != null) {
                    session.close()
                }
            }
        }

        CarType insertCarType(CarType carType) {
            log.debug("writing the carTYpe ${carType}")
            SqlSession session = null
            try {
                session = getSession()
                MapperInterface mapper = session.getMapper(MapperInterface.class)
                mapper.insertCarType(carType)
                log.debug("carType was inserted and is now ${carType}")
            } catch (Exception e) {
                log.error("Exception writing the carType", e)
            } finally {
                log.debug("closing the session")
                if (session != null) {
                    session.close()
                }
            }
            return carType
        }


        List<CarType> listCarTypes(String runId) {
            log.debug("getting a list of carTypes for runId ${runId}")
            SqlSession session = null
            List<CarType> retVal = null
            try {
                session = getSession()
                MapperInterface mapper = session.getMapper(MapperInterface.class)
                retVal = mapper.listCarTypes(runId)
            } catch (Exception e) {
                log.error("Exception retrieving the list of carTypes", e)
            } finally {
                log.debug("closing the session")
                if (session != null) {
                    session.close()
                }
            }
            return retVal
        }
    }
