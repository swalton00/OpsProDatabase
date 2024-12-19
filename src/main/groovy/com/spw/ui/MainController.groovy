package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.utility.PropertySaver
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
    RunTasks runit = RunTasks.getInstance()
    PropertySaver saver = PropertySaver.getInstance()
    DatabaseProcess db = DatabaseProcess.getInstance()

    boolean validFields() {
        boolean fieldsValid = false

        return fieldsValid
    }

    public void start() {
        mv = new MainView(this, mm)
        mm.setup()
        saver.init()
        String newValue = saver.getBaseString("userid")
        mm.userid.setText(newValue)
        newValue = saver.getBaseString("password")
        mm.pw.setText(newValue)
        newValue = saver.getBaseString("opsHome")
        mm.opsHome.setText(newValue)
        newValue = saver.getBaseString("schema")
        mm.schema.setText(newValue)
        newValue = saver.getBaseString("url")
        mm.url.setText(newValue)
        SwingUtilities.invokeLater({
            mv.start()
        })
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

    String currentUserid
    String currentPassword
    String currentSchema
    String currentOpsHome
    String currentURL
    String currentRunId
    String currentRunComment

    Runnable validatorTask = () -> {
        log.debug("first line of the validator task")
        if (SwingUtilities.isEventDispatchThread()) {
            log.error("still running under the EDT - returning")
            return
        }
        log.trace("about to run the database validate")
        boolean returnedValue = false

        returnedValue = db.validateFieslds(currentUserid, currentPassword, currentURL, currentSchema, mm.message)

        log.trace("database validate returned ${returnedValue}")
        if (returnedValue) {
            log.debug("all fields valid - saving the values")
            saver.putBaseString("userid", currentUserid)
            saver.putBaseString("password", currentPassword)
            saver.putBaseString("url", currentURL)
            saver.putBaseString("schema", currentSchema)
            saver.putBaseString("opsHome", currentOpsHome)
            saver.writeValues()
        }
        log.trace("switching back to ui thread to ")
        SwingUtilities.invokeLater {
            log.debug("invoked later ")
            mm.collectButton.setEnabled()
            mm.runReady = returnedValue
            if(mm.runReady & mm.validRunid) {
                log.trace("enabling Run now")
            }
        }
    }

    def buttonSaveValuesAction = { ActionEvent event ->
        log.debug("save values button pressed")
        currentUserid = mm.userid.getText()
        currentPassword = new String(mm.pw.getPassword())
        currentSchema = mm.schema.getText()
        currentOpsHome = mm.opsHome.getText()
        currentURL = mm.url.getText()
        mm.saveValues.setEnabled(false)
        runit.runIt(validatorTask)
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
