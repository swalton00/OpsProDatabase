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
import javax.swing.JOptionPane
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
        } else {
            log.debug("field ${fieldKey} is null - setting to empty string")
            field.setValue("")
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

    private void addListeners() {
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

    }

    private void saveFields() {
        log.debug("copying current values to saved values")
        mm.savedUserid = mm.userid.getValue()
        mm.savedPw = mm.password.getValue()
        mm.savedOpsHome = mm.opsHome.getValue()
        mm.savedSchema = mm.schema.getValue()
        mm.savedURL = mm.url.getValue()
        mm.savedRunId = mm.runId.getValue()
        mm.savedRunComment = mm.runComment.getValue()
    }

    Runnable initDatabase() {
        log.debug("initializing the database")
        verifyDatabase()
        if (mm.verifyPassed) {
            valuesSaved = true
            db.initialize(mm.savedURL,
                    mm.savedSchema,
                    mm.savedUserid,
                    mm.savedPw)
            db.validateFields(mm.savedURL,
                    mm.savedSchema,
                    mm.savedUserid,
                    mm.savedPw,
                    mm.message
            )  // complete database initialization
            mv.enableEntryFields(false)
            mv.changeResetButton(true)
            mv.changeSaveButton(false)
            if (mm.validRunId) {
                log.debug("Setting current stage to Collecting")
                mm.currentStage = MainModel.ProcessStage.COLLECTING
                mv.changeCollectButton(true)
                setupSequence()
            } else {
                log.debug("setting current stage to RUN_READY as runId is ${mm.runId.getValue()}")
                mm.currentStage = MainModel.ProcessStage.RUN_READY
                mm.message.setText("Enter a RunId to enable Collecting", Message.Level)
            }
        }
        return null
    }

    public void start() {
        mm.currentStage = MainModel.ProcessStage.INITIAL
        mv = new MainView(this, mm)
        mm.setup()
        saver.init()

        mm.validUserid = startField(mm.userid, "userid")
        mm.validPassword = startField(mm.password, "password")
        mm.validOpsHome = startField(mm.opsHome, "opsHome")
        mm.validSchema = startField(mm.schema, "schema")
        mm.validURL = startField(mm.url, "url")
        mm.validRunId = startField(mm.runId, "runId")
        mm.validRunComment = startField(mm.runComment, "runComment")
        mv.start()
        saveFields()
        checkAllFields()
        log.debug("all fields have been checked - result is ${mm.allFieldsValid} and RunId is ${mm.validRunId}")
        addListeners()
        if (mm.allFieldsValid) {
            initDatabase()
        }
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

    // Note: should already be on EDT
    def buttonResetAction = { ActionEvent event ->
        log.debug("got a request to reenable the entry fields")
        JOptionPane option = new JOptionPane("Resetting will require a restart - are you sure?")
        Object[] options = ["Reset", "Cancel"]
        Object selectedValue = JOptionPane.showOptionDialog(mv.mainFrame,
                "Resetting will require a restart - are you sure?",
                "Reset Question",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]
        )
        if (selectedValue == 0) {
            // chose yes to do it
            mv.enableEntryFields(true)
            mv.changeSaveButton(true)
            mv.changeResetButton(false)
            mv.changeViewButton(false)
            mv.changeCollectButton(false)
            mm.currentStage = MainModel.ProcessStage.RESET_VALUES
            log.debug("current stage is now RESET_VALUES")
        } else {
                log.debug("skipping reset - cancel option chosen")
                return
        }
    }

    def buttonCollectAction = { ActionEvent event ->
        log.debug("collection requested")
        mv.buttonCollect.setEnabled(false)
        runit.runIt(collectTask)
    }

    Runnable collectTask = () -> {
        log.debug("first line of the collect task")
        mm.message.setText("Collecting data")
        db.setRunId(mm.savedRunId, mm.savedRunComment)
        OpsReader ops = new OpsReader()
        ops.processFiles(mm.savedOpsHome, mm.savedRunId)
        setupSequence()
        SwingUtilities.invokeLater { ->
            log.debug("back from collection - reeneablling collect button")
            mv.buttonCollect.setEnabled(true)
            mm.message.setText("")
        }
    }

    Runnable createSelectTask = () -> {
        log.debug("creating the Select elements")
        SelectController sc = new SelectController(mm.savedRunId, mv.mainFrame)
        sc.init()
    }

    Runnable buttonSaveInner() {
        log.debug("all fields valid - saving the values - current stage is ${mm.currentStage}")
        verifyDatabase()
        if (!mm.verifyPassed) {
            log.debug("quitting button save as verify did not pass")
            return
        }
        saveFields()
        saver.putBaseString("userid", mm.savedUserid)
        saver.putBaseString("password", mm.savedPw)
        saver.putBaseString("url", mm.savedURL)
        saver.putBaseString("schema", mm.savedSchema)
        saver.putBaseString("opsHome", mm.savedOpsHome)
        saver.putBaseString("runId", mm.savedRunId)
        saver.putBaseString("runComment", mm.savedRunComment)
        saver.writeValues()
        if (mm.currentStage == MainModel.ProcessStage.RESET_VALUES) {
            log.debug("restarting after values reset")
            System.exit(0)
        }
        initDatabase()
        return null
    }

    def buttonSaveValuesAction = { ActionEvent event ->
        log.debug("save values button pressed")
        runit.runIt(buttonSaveInner())
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
                mm.password.getValue(),
                mm.url.getValue(),
                mm.message)
        if (goodValues) {
            log.debug("values for database all check out")
            mm.verifyPassed = true
            if (mm.currentStage == MainModel.ProcessStage.RESET_VALUES) {
                log.debug("now looking for all good fields in reset - enabling save")
                mv.changeSaveButton(true)
            }
        } else {
            mm.verifyPassed = false
        }
    }

    public void checkChange(String fieldName) {
        log.debug("checking change - current stage is ${mm.currentStage} and AllValid is ${mm.allFieldsValid}")
        if (fieldName.equals("runComment")) {
            mm.savedRunComment = mm.runComment.getValue()
            return
            // no need to check anything when runcomment changes
        }

        switch (mm.currentStage) {
            case MainModel.ProcessStage.INITIAL:
                if (mm.allFieldsValid) {
                    log.debug("all fields now valid -- enabling save")
                    mv.changeSaveButton(true)
                }
                break
            case MainModel.ProcessStage.LOOKING:
                // looking for the data to be entered
                if (mm.allFieldsValid) {
                    runit.runIt(initDatabase())
                }
                break
            case MainModel.ProcessStage.COLLECTING:
                if (fieldName.equals("runid")) {
                    saver.putBaseString("runId", mm.runId.getValue())
                    mm.savedRunId = mm.runId.getValue()
                    runit.runIt(setupSequence())
                }
                break
            case MainModel.ProcessStage.RESET_VALUES :
                if (mm.allFieldsValid) {
                    log.debug("all fields valid - now verifying database")
                    runit.runIt(verifyDatabase)
                }
                break
            default:
                log.error("dropped down after field change - whey?")
        }
    }

    /**
     * must be called in the background thread
     * read the count of sequences*/
    Runnable setupSequence() {
        saver.putBaseString("runId", mm.savedRunId)
        saver.putBaseString(("runComment"), mm.savedRunComment)
        mm.nextSequence = db.getSequence(mm.savedUserid, mm.savedPw, mm.savedURL, mm.savedSchema, mm.savedRunId)
        Integer seqCount = db.getSequenceCount(mm.savedUserid, mm.savedPw, mm.savedURL, mm.savedSchema, mm.savedRunId)
        mv.setSequenceText(mm.nextSequence.toString())
        if (seqCount > 0) {
            log.debug("sequence count is > 0 (${seqCount} - enabling views")
            mm.viewReady = true
        } else {
            mm.viewReady = false
        }
        mv.changeViewButton(mm.viewReady)
        mv.changeExportButton(mm.viewReady)
        return null
    }
}
