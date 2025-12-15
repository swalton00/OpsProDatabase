package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.rr.OpsReader
import com.spw.utility.PropertySaver
import com.spw.utility.RunTasks
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JFileChooser
import javax.swing.SwingUtilities
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
        mm.userid = newValue
        newValue = saver.getBaseString("password")
        mm.password = newValue
        newValue = saver.getBaseString("opsHome")
        mm.opsHome = newValue
        newValue = saver.getBaseString("schema")
        mm.schema = newValue
        newValue = saver.getBaseString("url")
        mm.url = newValue
        newValue = saver.getBaseString("runId")
        mm.runId = newValue
        newValue = saver.getBaseString("runComment")
        mm.runComment = newValue
        SwingUtilities.invokeLater({
            mv.start()
            mm.checkFields()
            if (mm.currentStage.equals(MainModel.ProcessStage.CHECKING)) {
                log.debug("already ready to run at startup")
                buttonSaveValuesAction(null as ActionEvent)
            }
        })
    }

    def buttonViewAction = { ActionEvent event ->
        log.debug("got a request from the view button")
        runit.runIt(createSelectTask)
    }

    Runnable writeProperties = () -> {
        saver.writeValues()
    }

    def buttonExitAction = { ActionEvent event ->
        log.debug("received an exit Event")
        runit.runIt(writeProperties)
        SwingUtilities.invokeLater { System.exit(0) }

    }

    def buttonCollectAction = { ActionEvent event ->
        log.debug("collection requested")
        mm.collectButton.setEnabled(false)
        runit.runIt(collectTask)
    }

    Runnable collectTask = () -> {
        log.debug("first line of the collect task")
        mm.message.setText("Collecting data")
        db.initialize( mm.savedURL + ";SCHEMA=" + mm.savedSchema, mm.savedUserid, mm.savedPw)
        db.setRunId(mm.savedRunId, mm.savedRunComment)
        OpsReader ops = new OpsReader()
        ops.processFiles(mm.savedOpsHome, mm.savedRunId)
        mm.getSequence()
        SwingUtilities.invokeLater { ->
            log.debug("back from collection - reeneablling collect button")
            mm.collectButton.setEnabled(true)
            mm.message.setText("")
        }
    }

    Runnable createSelectTask  = () -> {
        log.debug("creating the Select elements")
        SelectController sc = new SelectController(mm.savedRunId, mv.mainFrame)
        sc.init()
    }


    def radioAction = { ActionEvent event ->

    }

    Runnable validatorTask = () -> {
        log.debug("first line of the validator task")
        if (SwingUtilities.isEventDispatchThread()) {
            log.error("still running under the EDT - returning")
            return
        }
        log.trace("about to run the database validate")
        boolean returnedValue = false

        returnedValue = db.validateFieslds(mm.savedUserid, mm.savedPw, mm.savedURL, mm.savedSchema, mm.message)

        log.trace("database validate returned ${returnedValue}")
        if (returnedValue) {
            log.debug("all fields valid - saving the values")
            saver.putBaseString("userid", mm.savedUserid)
            saver.putBaseString("password", mm.savedPw)
            saver.putBaseString("url", mm.savedURL)
            saver.putBaseString("schema", mm.savedSchema)
            saver.putBaseString("opsHome", mm.savedOpsHome)
            if (!mm.savedRunId.isBlank()) {
                saver.putBaseString("runId", mm.savedRunId)
            }
            if (!mm.savedRunComment.isBlank()) {
                saver.putBaseString("runComment", mm.savedRunComment)
            }
            db.initialize(mm.savedURL + ";SCHEMA=" + mm.savedSchema, mm.savedUserid, mm.savedPw)
            saver.writeValues()
            if (mm.savedRunId.isBlank()) {
                mm.currentStage = MainModel.ProcessStage.RUN_READY
            } else {
                mm.currentStage = MainModel.ProcessStage.COLLECTING
            }
        }
        mm.checkRun()
    }

    def buttonSaveValuesAction = { ActionEvent event ->
        log.debug("save values button pressed")
        mm.savedUserid = mm.userid.getText()
        mm.savedPw = new String(mm.pw.getPassword())
        mm.savedSchema = mm.schema.getText()
        mm.savedOpsHome = mm.opsHome.getText()
        mm.savedURL = mm.url.getText()
        mm.savedRunId = mm.runId.getText()
        mm.savedRunComment = mm.runComment.getText()
        mm.saveValues.setEnabled(false)
        runit.runIt(validatorTask)
    }

    def selectHomeAction = { ActionEvent ->
        log.debug("showing a dialog to choose an OpsPro Home")
        JFileChooser chooser = new JFileChooser()
        boolean hiddenFiles = chooser.isFileHidingEnabled()
        chooser.setFileHidingEnabled(false)
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
