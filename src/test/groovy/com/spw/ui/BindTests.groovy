package com.spw.ui

import com.spw.utility.PropertySaver
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.slf4j.Logger

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

class BindTests {

    private static final Logger log = LoggerFactory.getLogger(BindTests.class)

    static PropertySaver saver = PropertySaver.getInstance()

    @BeforeAll
    static void setupAll() {
        saver.init()
    }

    @BeforeEach
    public void setup() {
        //mv.start()
    }

    @Test
    public void testForward() {
        MainController mc = new MainController()
        MainModel mm = new MainModel()
        MainView mv = new MainView(mc, mm)
        mv.start()
        println("     TEST1 - setting model")
        mm.userid.setValue("test1")
        println("mm.userid is ${mm.userid.getValue()}")
        println("the field value is ${mv.userid.getText()}")
        assertTrue(mm.userid.getValue().equals(mv.userid.getText()), "View should now have text")
    }

    @Test
    public void testReverse() {
        MainController mc = new MainController()
        MainModel mm = new MainModel()
        MainView mv = new MainView(mc, mm)
        mv.start()
        println("     TEST2 - setting View")
        mv.userid.setText("test2")
        println("mm.userid is ${mm.userid.getValue()}")
        println("the field value is ${mv.userid.getText()}")
        assertTrue(mm.userid.getValue().equals("test2"), "MainModel should now have test2")
    }

    @Test
    public void TestPWFroward() {
            MainController mc = new MainController()
            MainModel mm = new MainModel()
            MainView mv = new MainView(mc, mm)
            mv.start()
            println("     TEST3 - setting model")
            mm.password.setValue("test3")
            println("mm.password is ${mm.password.getValue()}")
            println("the field value is ${new String(mv.pw.getPassword())}")
            assertTrue(mm.password.getValue().equals("test3"), "MainModel should now have test2")
    }

    @Test
    public void testPWReverse() {
        MainController mc = new MainController()
        MainModel mm = new MainModel()
        MainView mv = new MainView(mc, mm)
        mv.start()
        println("     TEST4 - setting View")
        mv.pw.setText("test4")
        println("mm.password is ${mm.password.getValue()}")
        println("the field value is ${new String(mv.pw.getPassword())}")
        assertTrue(mm.password.getValue().equals("test4"), "MainModel should now have test2")
    }

    @Test
    public void testSchemaForward() {
        MainController mc = new MainController()
        MainModel mm = new MainModel()
        MainView mv = new MainView(mc, mm)
        mv.start()
        println("     TEST5 - setting Schema model")
        mm.schema.setValue("test5")
        println("mm.schema is ${mm.schema.getValue()}")
        println("the field value is ${mv.schema.getText()}")
        assertTrue(mm.schema.getValue().equals(mv.schema.getText()), "View should now have text")
    }

    @Test
    public void testSchemaReverse() {
        MainController mc = new MainController()
        MainModel mm = new MainModel()
        MainView mv = new MainView(mc, mm)
        mv.start()
        println("     TEST6 - setting Schema View")
        mv.schema.setText("test6")
        println("mm.schema is ${mm.schema.getValue()}")
        println("the field value is ${mv.schema.getText()}")
        assertTrue(mm.schema.getValue().equals("test6"), "MainModel should now have test2")
    }


}
