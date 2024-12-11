package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.utility.Message
import com.spw.utility.RunTasks
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
import java.awt.event.ActionEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener

@ToString(includeNames = true, includePackage = false, includeFields = true)
class MainModel implements FocusListener {

    private MainController mc = null
    private static final Logger log = LoggerFactory.getLogger(MainModel.class)
    DatabaseProcess db = DatabaseProcess.getInstance()
    RunTasks taskRunner = RunTasks.getInstance()


    public MainModel() {
    }

    void setup() {
        userid.addFocusListener(this)
        pw.addFocusListener(this)
        url.addFocusListener(this)
        schema.addFocusListener(this)
        db = DatabaseProcess.getInstance()
    }

    public MainModel(MainController mc) {
        this.mc = mc
        setup()
    }

    boolean readyToCheck = false
    boolean fieldsValid = false
    boolean dbConnected = false
    boolean runReady = false
    boolean viewReady = false

    JTextField userid = new JTextField("")
    JPasswordField pw = new JPasswordField("")
    JTextField url = new JTextField("")
    JTextField schema = new JTextField("")
    JTextField opsHome = new JTextField("")
    JTextField runId = new JTextField("")
    JTextField runComment = new JTextField("")
    JTextField currentSequence = new JTextField("")
    JButton exitButton = new JButton("Exit")
    JButton saveValues = new JButton("Save Values")
    JButton collectButton = new JButton("Collect Data")
    JButton buttonView = new JButton("View Data")
    JButton buttonExport = new JButton("Export Data")
    JButton buttonOpsHome = new JButton("Select Operations Home")
    JRadioButton radioCarByLoc = new JRadioButton("Cars by Location")
    JRadioButton radioLocByCar = new JRadioButton("Locations by car")
    Message message = new Message()
    JPanel messagePanel
    JLabel messageLabel

    @Override
    void focusGained(FocusEvent e) {
        return
    }

    @Override
    void focusLost(FocusEvent e) {
        log.debug("focus lost")
        if (!readyToCheck) return
        if (!fieldsValid) {
            log.debug("not fieldsValid is true")
            if (!(userid.getText().isBlank()
                    | (pw.getPassword().length == 0)
                    | url.getText().isBlank()
                    | schema.getText().isBlank()
                    | opsHome.getText().isBlank())) {
                log.debug("creating the task")
                Runnable validatorTask = () -> {
                    log.debug("first line of the validator task")
                    if (SwingUtilities.isEventDispatchThread()) {
                        log.debug("still running under the EDT - returning")
                        return
                    }
                    boolean returned = db.validateFields(userid.getText(),
                            new String(pw.getPassword()),
                            url.getText(),
                            schema.getText(),
                            message)
                    SwingUtilities.invokeLater {
                        log.debug("invoked later ")
                        fieldsValid = returned
                        if (fieldsValid) {
                            saveValues.setEnabled(true)
                        }
                    }
                }
                log.debug("about to submit")
                taskRunner.runIt(validatorTask)
            }
        } else {
            log.debug("focus lost but fielsValid is already true")

        }
    }
}


