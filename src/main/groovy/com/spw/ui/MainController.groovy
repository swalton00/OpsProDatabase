package com.spw.ui

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.SwingUtilities
import java.awt.event.ActionEvent

class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class)
    MainModel mm = new MainModel(this)
    MainView mv = null

    boolean validFields() {
        boolean fieldsValid = false

        return fieldsValid
    }

    public void start() {
        mv = new MainView(this, mm)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            void run() {
                mv.start()
            }
        })
    }

    def buttonViewAction = { ActionEvent event ->
        log.debug("got a reuest from the view button")

    }

    def buttonExportAction = { ActionEvent ->

    }

    def buttonExitAction = { ActionEvent event ->
        log.debug("received an exit Event")
        SwingUtilities.invokeLater { System.exit(0)}

    }

    def buttonCollectAction = { ActionEvent event ->

    }
    def radioAction = { ActionEvent event ->

    }

    def selectHomeAction = { ActionEvent ->

    }

}
