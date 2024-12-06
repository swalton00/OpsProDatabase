package com.spw.ui

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.SwingUtilities

class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class)
    MainModel mm = new MainModel()
    MainView mv = null

    public void start() {
        mv = new MainView(this, mm)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            void run() {
                mv.start()
            }
        })
    }


}
