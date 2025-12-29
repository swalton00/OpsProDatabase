package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.utility.Message
import com.spw.utility.ObservableString
import com.spw.utility.PropertySaver
import com.spw.utility.RunTasks
import com.sun.tools.javac.Main
import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.SwingUtilities

@ToString(includeNames = true, includePackage = false, includeFields = true)
class MainModel {

    private MainController mc = null
    private MainView mv = null
    private static final Logger log = LoggerFactory.getLogger(MainModel.class)
    private static final DatabaseProcess db = DatabaseProcess.getInstance()
    private static final PropertySaver saver = PropertySaver.getInstance()
    RunTasks taskRunner = RunTasks.getInstance()

    public MainModel(MainController mc) {
        this.mc = mc
        setup(mv)
    }

    String homeValue

    boolean readyToCheck = false
    boolean fieldsValid = false
    boolean homeValid = false
    boolean runReady = false
    boolean viewReady = false

    Boolean validUserid = false
    Boolean validPassword = false
    Boolean validSchema = false
    Boolean validOpsHome = false
    Boolean validURL = false
    Boolean verifyPassed = false
    Boolean allFieldsValid = false
    Boolean validRunId = false
    Boolean validRunComment = false

    Integer nextSequence = 0
    String savedUserid
    String savedPw
    String savedURL
    String savedSchema
    String savedRunId
    String savedRunComment
    String savedOpsHome

    ObservableString userid = new ObservableString("")
    ObservableString password = new ObservableString("")
    ObservableString url = new ObservableString("")
    ObservableString schema = new ObservableString("")

    ObservableString opsHome = new ObservableString("")
    ObservableString runId = new ObservableString("")
    ObservableString runComment = new ObservableString("")

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
        INITIAL, STARTUP, LOOKING, CHECKING, VERIFYREADY, SAVEREADY, NEED_RUNID, RUN_READY, COLLECTING
    }

    ProcessStage currentStage = ProcessStage.INITIAL
    Integer sequnceCount = 0


    public MainModel() {
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
        Integer seqCount = db.getSequenceCount(savedUserid, savedPw, savedURL, savedSchema, savedRunId)
        if (seqCount > 0) {
            log.debug("sequence count is > 0 (${seqCount} - enabling views")
            viewReady = true
        } else {
            viewReady = false
        }
        SwingUtilities.invokeLater { ->
            mv.currentSequence.setText(Integer.toString(nextSequence))
            if (viewReady) {
                log.debug('now enabling views')
                mv.buttonView.setEnabled(true)
                mv.buttonExport.setEnabled(true)
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

    void setup(MainView mv) {
        this.mv = mv
        log.trace("setup in MainModel is now complete")
    }


    /**
     * Inner cheeck of the values of runId - is it ready to enable the Collect button?
     *    MUST be run on the UI thread
     */
    Runnable innerCheckRun = () ->  {
       /* if (!runId.getText().isBlank()) {
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
        }*/
    }


}


