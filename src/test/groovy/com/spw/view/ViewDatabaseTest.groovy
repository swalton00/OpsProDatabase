package com.spw.view

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull

class ViewDatabaseTest  {

    static ViewDatabase db = ViewDatabase.getInstance()

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

}
