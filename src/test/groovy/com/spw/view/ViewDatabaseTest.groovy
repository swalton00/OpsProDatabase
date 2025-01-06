package com.spw.view

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.Logger

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull

class ViewDatabaseTest  {

    static ViewDatabase db = ViewDatabase.getInstance()
    private static final Logger log = LoggerFactory.getLogger(ViewDatabaseTest.class)

    @BeforeAll
    public static void setup() {
        db.initialize("jdbc:h2:C:/Projects/Ops_pro_Cars/Database/test;AUTO_SERVER=TRUE;SCHEMA=parser",
                "rr",
                "rrpass"
        )
    }

    @Test
    public void testCars() {
        List<ViewCar> results = db.listCars("B")
        assertNotNull(results)
    }

    @Test void testLocs() {
        List<ViewLoc> results = db.listViewLocs("B")
        assertNotNull(results)
        assertEquals(13, results.size())
        assertNotNull(results.get(0).tracks)
    }

    @Test
    public void testLocTrks() {
        List<ViewLoc> res = db.listViewLocs("B")
        log.debug("res list is ${res}")
        assertNotNull(res.get(0).tracks)
        log.debug("tracks of first is ${res.get(0).tracks.get(0)}")
        log.debug("tracks is ${res} and first is ${res.get(0)} while the trackes is ${res.get(0).tracks.get(0)}")
        assertNotNull(res.get(0).tracks.get(0))

    }

    @Test
    void testSequences() {
        List<Integer> results = db.getSequences('B')
        assertEquals(8, results.size())
    }

    @Test
    void testRowElements() {
        List<RowElement> results = db.getRowElements('B')
        log.debug("results were ${results}")
        log.debug("first element was ${results.get(0)}")
        assertNotNull(results)
        String toStrigValue = results.get(0).elements.get(0).toString()
        log.debug("first row is ${results.get(0).elements.get(0)}")
        assertEquals(8, results.get(0).elements.size())
    }

    @Test
    void testRowLocs() {
        List<RowElement> results = db.getRowLocs('B')
        ViewElement.setType(ViewElement.RunType.TRACK, true, true)
       // log.debug("Results were ${results}")
        log.debug("first row is ${results.get(0).elements.get(0)}")
        assertEquals(37, results.size())
    }

}
