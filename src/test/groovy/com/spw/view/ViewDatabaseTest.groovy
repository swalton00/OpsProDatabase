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
        assertNotNull(res.get(0).tracks.get(0))

    }

    @Test
    void testSequences() {
        List<Integer> results = db.getSequences('B')
        assertEquals(8, results.size())
    }

}
