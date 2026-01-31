package com.spw.view

import com.spw.mappers.CarType
import com.spw.rr.AbstractDatabase
import com.spw.rr.DatabaseProcess
import com.spw.view.ViewCar
import com.spw.view.ViewInterface
import groovy.transform.ToString
import org.apache.ibatis.session.SqlSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@ToString(includeFields = true, includePackage = false, includeNames = true)
@Singleton
class ViewDatabase {

    static final mainDB = DatabaseProcess.getInstance()

    private static final Logger log = LoggerFactory.getLogger(ViewDatabase.class)

    public List<ViewCar> listCars(String runId) {
        SqlSession session = null
        try {
            session = mainDB.getSession()
            ViewInterface map = session.getMapper(ViewInterface.class)
            List<ViewCar> carList = map.listViewCars(runId)
            log.debug("got a list of ${carList.size()}")
            return carList
        } catch (Exception e) {
            log.error("Exception processing a View ", e)
        } finally {
            if (session != null) {
                session.close()
            }
        }
        return null
    }


    public List<ViewLoc> listViewLocs(String runId) {
        SqlSession session = null
        try {
            session = mainDB.getSession()
            ViewInterface map = session.getMapper(ViewInterface.class)
            List<ViewLoc> locs = map.listViewLocs(runId)
            log.debug("got a list of ${locs.size()}")
            return locs
        } catch (Exception e) {
            log.error("Exception processing a View ", e)
        } finally {
            if (session != null) {
                session.close()
            }

        }
        return null
    }

    public List<Integer> getSequences(String runId) {
        SqlSession session = null
        try {
            session = mainDB.getSession()
            ViewInterface map = session.getMapper(ViewInterface.class)
            List<Integer> results = map.getSequenceNumbers(runId)
            log.debug("returning a list of sequnce numbers with ${results.size()} entries on it")
            return results
        } catch (Exception e) {
            log.error("Exception processing a View ", e)
        }
        finally {
            if (session != null) {
                session.close()
            }
        }
        return null
    }

    public List<RowElement> getRowElements(ViewParameter parameters) {
        SqlSession session = null
        try {
            session = mainDB.getSession()
            ViewInterface map = session.getMapper(ViewInterface.class)
            List<RowElement> results = map.listRows(parameters)
            log.debug("got a result set with ${results} rows")
            return results
        } catch (Exception e) {
            log.error("Exception processing a View ", e)
        }
        finally {
            if (session != null) {
                session.close()
            }
        }
        return null
    }

    public List<RowElement> getRowLocs(ViewParameter parameter) {
        SqlSession session = null
        try {
            session = mainDB.getSession()
            ViewInterface map = session.getMapper(ViewInterface.class)
            List<RowElement> results = map.listLocRows(parameter)
            log.debug("the resutts were ${results.size()}")
            return results
        } catch (Exception e) {
            log.error("Exception processing a View ", e)
        } finally {
            if (session != null) {
                session.close()
            }
        }
        return null
    }

    public List<ViewType> listCarTypes(String runId) {
        SqlSession session = null
        try {
            session = mainDB.getSession()
            ViewInterface map = session.getMapper(ViewInterface.class)
            List<ViewType> results = map.listViewTypes(runId)
            log.debug("the results were ${results.size()}")
            return results
        } catch (Exception e) {
            log.error("Exception processing a View ", e)
        } finally {
            if (session != null) {
                session.close()
            }
        }
        return null
    }

    public Integer getMoveCount(String runId) {
        SqlSession session = null
        try {
            session = mainDB.getSession()
            ViewInterface map = session.getMapper(ViewInterface.class)
            Integer moveCount = map.getMoveCount(runId)
            log.debug("the resutts were ${moveCount}")
            return moveCount
        } catch (Exception e) {
            log.error("Exception processing getMoveCount ", e)
        } finally {
            if (session != null) {
                session.close()
            }
        }
        return null
    }
}
