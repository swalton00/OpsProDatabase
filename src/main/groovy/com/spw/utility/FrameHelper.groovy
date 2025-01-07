package com.spw.utility

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.awt.Component
import java.awt.Dimension
import java.awt.Point
import java.awt.Window
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener

class FrameHelper  implements ComponentListener {
    private static final Logger log = LoggerFactory.getLogger(OpDialog.class)
    private static final PropertySaver saver = PropertySaver.getInstance()

    @Override
    void componentResized(ComponentEvent e) {
        Component source = e.getComponent()
        String name = source.getName()
       // log.trace("component resized event for ${name}")
        Dimension dim = source.getSize()

        saver.saveInt(name, OpDialog.getWidthName(), (int)dim.getWidth())
        saver.saveInt(name, OpDialog.getHeightName(), (int)dim.getHeight())
    }

    @Override
    void componentMoved(ComponentEvent e) {
        Component source = e.getComponent()
        String name = source.getName()
     //  log.trace("component moved event for ${name}")
        Point p = e.getComponent().getLocation()
        saver.saveInt(name, OpDialog.getXname(), (int) p.getX())
        saver.saveInt(name, OpDialog.getYname(), (int) p.getY())
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
