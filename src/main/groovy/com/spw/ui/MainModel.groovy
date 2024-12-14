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
        opsHome.addFocusListener(this)
        db = DatabaseProcess.getInstance()
    }

    public MainModel(MainController mc) {
        this.mc = mc
        setup()
    }

    String homeValue

    boolean readyToCheck = false
    boolean fieldsValid = false
    boolean  homeValid = false
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

    Runnable checkOpsHome = () -> {
        boolean resultValue = false
        log.debug("checking ops home value of ${homeValue}")
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
        log.debug("result was validation was ${homeValid}")
    }

    @Override
    void focusLost(FocusEvent e) {
        log.debug("focus lost")
        if (!readyToCheck) return
        if ("opshome".equals(e.getSource().getName()) ) {
            log.debug("validing new Ops Home location")
            homeValue = opsHome.getText()
            taskRunner.runIt(checkOpsHome)
        }
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


