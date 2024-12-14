package com.spw.ui

import com.spw.utility.RunTasks
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import javax.swing.plaf.FileChooserUI
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
        SwingUtilities.invokeLater({
            mv.start()
        } )
    }

    def buttonViewAction = { ActionEvent event ->
        log.debug("got a request from the view button")

    }

    def buttonExportAction = { ActionEvent ->

    }

    def buttonExitAction = { ActionEvent event ->
        log.debug("received an exit Event")
        SwingUtilities.invokeLater { System.exit(0) }

    }

    def buttonCollectAction = { ActionEvent event ->

    }
    def radioAction = { ActionEvent event ->

    }

    def selectHomeAction = { ActionEvent ->
        log.debug("showing a dialog to choose an OpsPro Home")
        JFileChooser chooser = new JFileChooser()
        chooser.setDialogTitle("Select OpsPro Home Directory")
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
        int returnValue = chooser.showDialog(null, "Select OpsPro Home")
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File chosen = chooser.getSelectedFile()
            log.debug("selected file was ${chosen.toString()}")
            mm.opsHome.setText(chosen.toString())
        } else {
            log.debug("selection was canceled ")
        }

    }

}
