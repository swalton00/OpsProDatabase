package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.utility.Message
import com.spw.utility.PropertySaver
import com.spw.utility.RunTasks
import groovy.beans.Bindable
import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.JRadioButton
import javax.swing.JTextField
import javax.swing.SwingUtilities
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

@ToString(includeNames = true, includePackage = false, includeFields = true)
class MainModel implements FocusListener {

    private MainController mc = null
    private static final Logger log = LoggerFactory.getLogger(MainModel.class)
    private static final DatabaseProcess db = DatabaseProcess.getInstance()
    private static final PropertySaver saver = PropertySaver.getInstance()
    RunTasks taskRunner = RunTasks.getInstance()

    public MainModel(MainController mc) {
        this.mc = mc
        setup()
    }

    String homeValue

    boolean readyToCheck = false
    boolean fieldsValid = false
    boolean homeValid = false
    boolean runReady = false
    boolean viewReady = false

    @Bindable Boolean validHome = false
    @Bindable Boolean validUserid = false
    @Bindable Boolean validPassword = false
    @Bindable Boolean validSchema = false
    @Bindable Boolean validURL = false

    Integer nextSequence = 0
    String savedUserid
    String savedPw
    String savedURL
    String savedSchema
    String savedRunId
    String savedRunComment
    String savedOpsHome

    @Bindable String userid = ""
    @Bindable String password = ""
    @Bindable String url = ""
    @Bindable String schema = ""

    @Bindable String opsHome
    @Bindable String runId
    @Bindable String runComment
    //@Bindable String message

    Message message = new Message()


    String priorValue
    String newValue
    Boolean valueChanged

    /*
            four stages
                1. before ui displayed (don'ct check anything)
                2. before all fields are valid (check opsHome, userid, pw, url, schema)
                3. after enabling SaveValues (check for runId set
                4. after enbaling dataCollect
     */

    public enum ProcessStage {
        INITIAL, CHECKING, RUN_READY, COLLECTING
    }

    ProcessStage currentStage = ProcessStage.INITIAL
    Integer sequnceCount = 0


    public MainModel() {
    }

    void checkFields() {
        if (SwingUtilities.isEventDispatchThread()) {
           //  innerCheckFields()
        } else {
            SwingUtilities.invokeLater { ->
         //       innerCheckFields()
            }
        }

    }

    private void innerCheckFields() {
        boolean foundError = false
        if (currentStage == ProcessStage.CHECKING) {
            foundError = false
            String msgText = ""
            if (userid.getText().isBlank()) {
                validUserid = false
            } else {
                validUserid = true
            }
            if (new String(pw.getPassword()).isBlank()) {
                validPassword = false
            } else {
                validPassword = true
            }
            if (url.getText().isBlank()) {
                validURL = false
            } else {
                if (url.getText().startsWith("jdbc:h2:")) {
                    if (url.getText().contains(";SCHEMA=")) {
                        log.trace("URL contains schema")
                        foundError = true
                        msgText = "URL should not contain the schema (will be set internally)"
                    } else {
                        validURL = true
                    }
                } else if (url.getText().startsWith("jdbc:")) {
                    log.trace("possible incorrect database - only H2 supported - URL should start with 'jdbc:h2:'")
                    foundError = true
                    msgText = "Possible incorrect database - only H2 supported 'jdbc:h2:....'"
                } else {
                    log.trace("url wrong format - doesn't start with 'jdbc:'")
                    foundError = true
                    msgText = "Incorrect URL format - should start with 'jdbc:'"
                }
            }
            if (schema.getText().isBlank()) {
                validSchema = false
            } else {
                validSchema = true
            }
            if (foundError) {
                message.setText(msgText, Message.Level.ERROR)
            } else {
                if (validHome &
                        validSchema &
                        validURL &
                        validPassword &
                        validUserid) {
                    message.setText("")
                    fieldsValid = true
                    saveValues.setEnabled(true)
                }
            }
            if (!validHome & !opsHome.getText().isBlank()) {
                homeValue = opsHome.getText()
                taskRunner.runIt(checkOpsHome)
            }
        } else if (currentStage == ProcessStage.RUN_READY) {
            if (!runId.getText().isBlank()) {
                log.debug("ready to move on to collecting")
                collectButton.setEnabled(true)
                currentStage = ProcessStage.COLLECTING
            }
        }
    }

    /**
     * must be called in the background thread
     * read the count of sequences
     */
    Runnable getSequence = () ->  {
        if (SwingUtilities.isEventDispatchThread()) {
            log.error("getSequence called from UI thread!")
            return
        }
        saver.putBaseString("runId", savedRunId)
        saver.putBaseString(("runComment"), savedRunComment)
        nextSequence = db.getSequence(savedUserid, savedPw, savedURL, savedSchema, savedRunId)
        Integer seqCount = db.getSequnceCount(savedUserid, savedPw, savedURL, savedSchema, savedRunId)
        if (seqCount > 0) {
            log.debug("sequence count is > 0 (${seqCount} - enabling views")
            viewReady = true
        } else {
            viewReady = false
        }
        SwingUtilities.invokeLater { ->
            currentSequence.setText(Integer.toString(nextSequence))
            if (viewReady) {
                log.debug('now enabling views')
                buttonView.setEnabled(true)
                buttonExport.setEnabled(true)
            }
        }
    }

    String checkNotNull(String key) {
        String temp = saver.getBaseString(key)
        if (temp == null) {
            log.debug("setting ${key} to empty string")
            temp = ""
        }
        return temp
    }

    void setup() {
 /*       userid.setText(checkNotNull("userid"))
        pw.setText(checkNotNull("password"))
        url.setText(checkNotNull("url"))
        schema.setText(checkNotNull("schema"))
        opsHome.setText(checkNotNull("opsHome"))
        userid.addFocusListener(this)
        pw.addFocusListener(this)
        url.addFocusListener(this)
        schema.addFocusListener(this)
        opsHome.addFocusListener(this)
        runId.addFocusListener(this)*/
        checkFields()
    }

    @Override
    void focusGained(FocusEvent e) {
        if (e.getComponent().class == JTextField.class) {
            priorValue = e.getComponent().getText()
        } else if (e.getComponent().getClass() == JPasswordField) {
            priorValue = new String(e.getComponent().getPassword())
        }
        return
    }

    void checkRun() {
        if (SwingUtilities.isEventDispatchThread()) {
            innerCheckRun()
        } else {
            SwingUtilities.invokeLater(innerCheckRun)
        }
    }

    /**
     * Inner cheeck of the values of runId - is it ready to enable the Collect button?
     *    MUST be run on the UI thread
     */
    Runnable innerCheckRun = () ->  {
        if (!runId.getText().isBlank()) {
            log.debug("runId field is non-blank - ready to move to next stage")
            currentStage = ProcessStage.COLLECTING
            collectButton.setEnabled(true)
            message.setText("", Message.Level.INFO)
            savedRunId = runId.getText()
            savedRunComment = runComment.getText()
            taskRunner.runIt(getSequence)
        } else {
            message.setText("Enter a runid to enable collecting data", Message.Level.INFO)
            currentStage = ProcessStage.RUN_READY
        }
    }

    Runnable checkOpsHome = () -> {
        log.debug("checking ops home value of ${homeValue}")
        boolean resultValue = false
        try {
            File homeLocation = new File(homeValue)
            if (!homeLocation.exists()) {
                message.setText("New Ops Home location does not exist", Message.Level.ERROR)
                return
            }
            if (!homeLocation.isDirectory()) {
                message.setText("New Ops Home Location must be a directory", Message.Level.ERROR)
                return
            }
            File carRoster = new File(homeLocation, "OperationsCarRoster.xml")
            if (!(carRoster.exists() & carRoster.canRead())) {
                message.setText("Car Roster not found in Ops Home location:", Message.Level.ERROR)
                return
            }
            File locationRoster = new File(homeLocation, "OperationsLocationRoster.xml")
            if (!(locationRoster.exists() & locationRoster.canRead())) {
                message.setText("Location xml file not found in Ops Home location", Message.Level.ERROR)
                return
            }
            message.setText("      ", Message.Level.INFO)
            validHome = true
            resultValue = true
            log.trace("Ops Home now valid")
        } catch (Exception e) {
            if (e.getClass() == FileNotFoundException.class) {
                message.setText("Ops Home file not found exception", Message.Level.ERROR)
                return
            } else {
                log.error("Exception validating Ops Home - value was ${homeValue}", e)
                message("Exception attempting to validate the new Ops Home setting")
            }
        }
        homeValid = resultValue
        if (homeValid) {
            validHome = true
            checkFields()
        }
        log.debug("result was validation was ${homeValid}")
    }

     @Override
    void focusLost(FocusEvent e) {
        log.debug("focus lost")
        if (e.getComponent().getClass().equals(JPasswordField.class)) {
            newValue = new String(e.getComponent().getPassword())
        } else {
            newValue  = e.getComponent().getText()
        }
        if (!newValue.equals(priorValue)){
            valueChanged = true
        } else {
            valueChanged = false
        }
        if (currentStage.equals(ProcessStage.CHECKING)) {
            log.debug("focus lost and current stage is ${currentStage} value should be ${ProcessStage.CHECKING}")
           // innerCheckFields()
        } else if (currentStage.equals(ProcessStage.COLLECTING) | currentStage.equals(ProcessStage.RUN_READY)) {
            log.debug("current stage is RUN_READY and got focus lost")
            if (e.getComponent().getName().equals("runid")) {
                if (!newValue.equals(priorValue)) {
                    checkRun()
                }
            }
        }
    }
}


