package com.spw.ui

import com.spw.rr.OpsReader
import com.spw.utility.PropertySaver
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFrame
import java.awt.Toolkit
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.JTextField
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import java.awt.GridBagConstraints
import java.awt.GridBagLayout

class OpsProgress {
    private static final Logger log = LoggerFactory.getLogger(OpsProgress.class)

    static void main(String[] args) {
        log.trace("starting the application")
        PropertySaver.getInstance().init()
        MainController mc = new MainController()
        mc.start()
    }
}
