package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.rr.OpsReader
import com.spw.utility.Message
import com.spw.utility.ObservableString
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
    boolean valuesSaved = false

    Runnable checkOpsHome = () -> {
        log.debug("checking ops home value of ${mm.opsHome.getValue()}")
        mm.validOpsHome = false
        try {
            File homeLocation = new File(mm.opsHome.getValue())
            if (!homeLocation.exists()) {
                mm.message.setText("New Ops Home location does not exist", Message.Level.ERROR)
                return
            }
            if (!homeLocation.isDirectory()) {
                mm.message.setText("New Ops Home Location must be a directory", Message.Level.ERROR)
                return
            }
            File carRoster = new File(homeLocation, "OperationsCarRoster.xml")
            if (!(carRoster.exists() & carRoster.canRead())) {
                mm.message.setText("Car Roster not found in Ops Home location:", Message.Level.ERROR)
                return
            }
            File locationRoster = new File(homeLocation, "OperationsLocationRoster.xml")
            if (!(locationRoster.exists() & locationRoster.canRead())) {
                mm.message.setText("Location xml file not found in Ops Home location", Message.Level.ERROR)
            }
            mm.message.setText("      ", Message.Level.INFO)
            mm.validOpsHome = true
            log.trace("Ops Home now valid")
        } catch (Exception e) {
            if (e.getClass() == FileNotFoundException.class) {
                mm.message.setText("Ops Home file not found exception", Message.Level.ERROR)
                return
            } else {
                log.error("Exception validating Ops Home - value was ${mm.opsHome.getValue()}", e)
                mm.message.setText("Exception attempting to validate the new Ops Home setting")
            }
        }
        log.debug("result was validation was ${mm.validOpsHome}")
    }

    private boolean startField(ObservableString field, String fieldKey) {
        String newValue = saver.getBaseString(fieldKey)
        if (newValue != null) {
            field.setValue(newValue)
            if (!newValue.isBlank()) {
                return true
            } else {
                return false
            }

        }
        return false
    }

    private boolean checkOneField(ObservableString field) {
        if (field.getValue().length() > 0) {
            return true
        } else {
            return false
        }
    }

    private checkAllFields() {
        if (mm.validUserid & mm.validPassword & mm.validURL & mm.validOpsHome & mm.validSchema) {
            mm.allFieldsValid = true
            log.debug("now setting All Fields Valid to TRUE")
        }
    }

    public void start() {
        mm.currentStage = MainModel.ProcessStage.INITIAL
        mv = new MainView(this, mm)
        mm.setup(mv)
        if (saver.init()) {
            mm.validUserid = startField(mm.userid, "userid")
            mm.validPassword = startField(mm.password, "password")
            mm.validOpsHome = startField(mm.opsHome, "opsHome")
            mm.validSchema = startField(mm.schema, "schema")
            mm.validURL = startField(mm.url, "url")
            mm.validRunId = startField(mm.runId, "runId")
            checkAllFields()
            mm.validRunComment =  startField(mm.runComment, "runComment")
        } else {
            mm.currentStage = MainModel.ProcessStage.LOOKING
        }
        mm.userid.addPropertyChangeListener {
            mm.validUserid = checkOneField(mm.userid)
            checkAllFields()
        }
        mm.password.addPropertyChangeListener {
            mm.validPassword = checkOneField(mm.password)
            checkAllFields()
        }
        mm.schema.addPropertyChangeListener {
            mm.validSchema = checkOneField(mm.schema)
            checkAllFields()
            if (mm.currentStage.equals(MainModel.ProcessStage.SAVEREADY)) {
                mv.collectButton.setEnabled(true)
                mv.saveValues.setEnabled(false)
                mm.currentStage = MainModel.ProcessStage.COLLECTING
            }
        }
        mm.url.addPropertyChangeListener {
            String temp = mm.url.getValue()
            if (temp.startsWith("jdbc:h2:") & temp.length() > "jdbc:h2:".length()) {
                mm.validURL = true
                checkAllFields()
                mm.message.setText("", Message.Level.INFO)
            } else {
                mm.validURL = false
                mm.message.setText("URL must start with 'jdbc:h2:", Message.Level.ERROR)
            }
        }
        mm.opsHome.addPropertyChangeListener {
            runit.runIt(checkOpsHome)
            checkAllFields()
        }
        mm.runId.addPropertyChangeListener {
            mm.validRunId = checkOneField(mm.runId)
            checkAllFields()
        }
        mm.runComment.addPropertyChangeListener {
            mm.validRunComment = checkOneField(mm.runComment)
        }
        if (mm.allFieldsValid) {
            // run verify to check fields from Properties file
            verifyDatabase
            if (mm.verifyPassed) {
                valuesSaved = true
                db.initialize(mm.url.getValue(), mm.schema.getValue(),
                        mm.userid.getValue(), mm.password.getValue())
            }
        }
        SwingUtilities.invokeLater({
            mv.start()
            if (mm.currentStage == MainModel.ProcessStage.RUN_READY | mm.currentStage == MainModel.ProcessStage.COLLECTING) {
                log.debug("all fields valid at startup")
                mm.currentStage = MainModel.ProcessStage.NEED_RUNID
                if (mm.validRunId) {
                    mm.currentStage = MainModel.ProcessStage.COLLECTING
                    mv.collectButton.setEnabled(true)
                    runit.runIt(mm.getSequence)
                } else {
                    mm.currentStage = MainModel.ProcessStage.RUN_READY
                    mm.message.setText("Enter a Run Id value to start collecting", Message.Level.INFO)
                }
            } else {
                log.debug("setting current stage to LOOKING at startup")
                mm.currentStage = MainModel.ProcessStage.LOOKING
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
        mv.collectButton.setEnabled(false)
        runit.runIt(collectTask)
    }

    Runnable collectTask = () -> {
        log.debug("first line of the collect task")
        mm.message.setText("Collecting data")
        db.setRunId(mm.savedRunId, mm.savedRunComment)
        OpsReader ops = new OpsReader()
        ops.processFiles(mm.savedOpsHome, mm.savedRunId)
        mm.getSequence()
        SwingUtilities.invokeLater { ->
            log.debug("back from collection - reeneablling collect button")
            mv.collectButton.setEnabled(true)
            mm.message.setText("")
        }
    }

    Runnable createSelectTask = () -> {
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
        mm.verifyPassed = false

        mm.verifyPassed = db.validateFields(mm.userid.getValue(),
                mm.password.getValue(),
                mm.url.getValue(),
                mm.schema.getValue(),
                mm.message)

        log.trace("database validate returned ${mm.verifyPassed}")
        if (mm.verifyPassed) {
            mm.currentStage = MainModel.ProcessStage.COLLECTING
            mv.saveValues.setEnabled(false)
            mv.collectButton.setEnabled(true)
        } else {
            mm.currentStage = MainModel.ProcessStage.LOOKING
            mv.saveValues.setEnabled(false)
        }
    }

    Runnable saveValues = () -> {
        log.debug("all fields valid - saving the values")
        saver.putBaseString("userid", mm.userid.getValue())
        saver.putBaseString("password", mm.password.getValue())
        saver.putBaseString("url", mm.url.getValue())
        saver.putBaseString("schema", mm.schema.getValue())
        saver.putBaseString("opsHome", mm.opsHome.getValue())
        if (mm.validRunId) {
            saver.putBaseString("runId", mm.runId.getValue())
        }
        if (mm.verifyPassed) {
            if (!mm.runId.getValue().isBlank()) {
                saver.putBaseString("runId", mm.runId.getValue())
            }
            if (!mm.runComment.getValue().isBlank()) {
                saver.putBaseString("runComment", mm.runComment.getValue())
            }
            db.initialize(mm.url.getValue(), mm.schema.getValue(), mm.userid.getValue(), mm.password.getValue())
            saver.writeValues()
            if (mm.runId.getValue().isBlank()) {
                mm.currentStage = MainModel.ProcessStage.RUN_READY
            } else {
                mm.currentStage = MainModel.ProcessStage.COLLECTING
            }
        }
        if (SwingUtilities.isEventDispatchThread()) {
            mv.saveValues.setEnabled(false)
        } else {
            SwingUtilities.invokeLater {
                mv.saveValues.setEnabled(false)
            }
        }
    }

    def buttonSaveValuesAction = { ActionEvent event ->
        log.debug("save values button pressed")
        runit.runIt(validatorTask)
        mm.savedUserid = mm.userid.getValue()
        mm.savedPw = mm.password.getValue()
        mm.savedSchema = mm.schema.getValue()
        mm.savedOpsHome = mm.opsHome.getValue()
        mm.savedURL = mm.url.getValue()
        mm.savedRunId = mm.runId.getValue()
        mm.savedRunComment = mm.runComment.getValue()
        mv.saveValues.setEnabled(false)
        runit.runIt(saveValues)
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
            mm.opsHome.setValue(chosen.toString())
        } else {
            log.debug("selection was canceled ")
        }

    }

    Runnable verifyDatabase = () -> {
        log.debug("verifying database values now")
        boolean goodValues = db.verifyConnect(mm.userid.getValue(),
                mm.password.getValue(), mm.url.getValue())
        if (goodValues) {
            log.debug("values for database all check out")
            SwingUtilities.invokeLater { ->
                mm.verifyPassed = true
                mm.currentStage = MainModel.ProcessStage.SAVEREADY
                mv.saveValues.setEnabled(true)
            }
        }
    }

    public void checkChange(String fieldName) {
        log.debug("checking change - current stage is ${mm.currentStage} and AllValid is ${mm.allFieldsValid}")
        if (fieldName.equals("runComment")) {
            return
            // no need to check anything when runcomment changes
        }

        switch (mm.currentStage) {
            case MainModel.ProcessStage.INITIAL:
                // ignore everything while initializing
                break
            case MainModel.ProcessStage.LOOKING:
                // looking for the data to be entered
                if (mm.allFieldsValid) {
                    runit.runIt(verifyDatabase)
                }
                break
            case MainModel.ProcessStage.COLLECTING:
            case MainModel.ProcessStage.SAVEREADY:
                if (fieldName.equals("runid")) {
                    saver.putBaseString("runId", mm.runId.getValue())
                }
                break
            default:
                log.error("dropped down after field change - whey?")
        }
    }
}
