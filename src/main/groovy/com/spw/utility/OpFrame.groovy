package com.spw.utility

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFrame
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener

class OpFrame extends JFrame implements ComponentListener{
    private static final Logger log = LoggerFactory.getLogger(OpFrame.class)
    private static final PropertySaver saver = PropertySaver.getInstance()
    private static final String NAME_HEIGHT = "sizeHeight"
    private static final String NAME_WIDTH = "sizeWidth"
    private static final String NAME_X = "x"
    private static final String NAME_Y = "y"

    String getXname() {
        return NAME_X
    }

    String getYname() {
        return NAME_Y
    }

    String getWidthName() {
        return NAME_WIDTH
    }

    String getHeightName() {
        return NAME_HEIGHT
    }


    @Override
    void componentResized(ComponentEvent e) {
        String name = e.getComponent().getName()
        log.trace("component resized event for ${name}")
        Dimension dim = e.getComponent().getSize()
        saver.saveInt(name, "sizeWidth", dim.getWidth())
        saver.saveInt(name, "sizeHeighth")
    }

    @Override
    void componentMoved(ComponentEvent e) {
        String name = e.getComponent().getName()
        log.trace("component moved event for ${name}")
        Point p = e.getComponent().getLocation()
        saver.saveInt(name, NAME_X, (int) p.getX())
        saver.saveInt(name, NAME_Y, (int) p.getY())
    }

    @Override
    void componentShown(ComponentEvent e) {
        return
    }

    @Override
    void componentHidden(ComponentEvent e) {
        return
    }
}
