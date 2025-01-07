package com.spw.utility

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JDialog
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ComponentEvent
import java.awt.event.ComponentListener

class OpDialog extends JDialog {
    private static final String NAME_HEIGHT = "sizeHeight"
    private static final String NAME_WIDTH = "sizeWidth"
    private static final String NAME_X = "x"
    private static final String NAME_Y = "y"

    OpDialog(java.awt.Dialog parent, String title, boolean modal) {
        super(parent, title, modal)
        FrameHelper helper = new FrameHelper()
        this.addComponentListener(helper)
    }

    OpDialog() {
        super()
    }


    public static String getXname() {
        return NAME_X
    }

    public static String getYname() {
        return NAME_Y
    }

    public static String getWidthName() {
        return NAME_WIDTH
    }

    public static String getHeightName() {
        return NAME_HEIGHT
    }

}
