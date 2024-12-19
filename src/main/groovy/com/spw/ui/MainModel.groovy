package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.utility.Message
import com.spw.utility.PropertySaver
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
import java.util.regex.Pattern

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
    boolean dbConnected = false
    boolean runReady = false
    boolean viewReady = false

    boolean validHome = false
    boolean validUserid = false
    boolean validPassword = false
    boolean validSchema = false
    boolean validURL = false
    boolean validRunid = false

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

    String priorValue


    public MainModel() {
    }

    void checkFields() {
        if (SwingUtilities.isEventDispatchThread()) {
            innerCheckFields()
        } else {
            SwingUtilities.invokeLater { ->
                innerCheckFields()
            }
        }
    }

    private void innerCheckFields() {
        boolean foundError = false
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
        userid.setText(checkNotNull("userid"))
        pw.setText(checkNotNull("password"))
        url.setText(checkNotNull("url"))
        schema.setText(checkNotNull("schema"))
        opsHome.setText(checkNotNull("opsHome"))
        userid.addFocusListener(this)
        pw.addFocusListener(this)
        url.addFocusListener(this)
        schema.addFocusListener(this)
        opsHome.addFocusListener(this)
        runId.addFocusListener(this)
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
        if (!readyToCheck) return
        innerCheckFields()
    }
}


