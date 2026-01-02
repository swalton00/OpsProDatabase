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
    private static final Logger log = LoggerFactory.getLogger(MainModel.class)
    private static final DatabaseProcess db = DatabaseProcess.getInstance()
    private static final PropertySaver saver = PropertySaver.getInstance()

    public MainModel(MainController mc) {
        this.mc = mc
        setup()
    }

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
    String newValue

    /*
            five stages
                1. INITIAL before ui displayed (don'ct check anything)
                2. LOOKING before all fields are valid (check opsHome, userid, pw, url, schema)
                3. RUN_READY after enabling SaveValues (check for runId set
                4. COLLECTING after enbaling dataCollect
                5. RESET_VALUES renabling entry fields for new values (requires restart)
     */
    public enum ProcessStage {
        INITIAL, LOOKING, RUN_READY, COLLECTING, RESET_VALUES
    }

    ProcessStage currentStage = ProcessStage.INITIAL
    Integer sequnceCount = 0

    public MainModel() {
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
        log.trace("setup in MainModel is now complete")
    }
}


