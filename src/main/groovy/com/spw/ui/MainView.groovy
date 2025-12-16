package com.spw.ui

import com.spw.utility.FrameHelper
import com.spw.utility.Message
import com.spw.utility.ObservableString
import com.spw.utility.OpDialog
import com.spw.utility.PropertySaver
import net.miginfocom.swing.MigLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import java.awt.*
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class MainView implements DocumentListener {
    MainController mc
    MainModel mm
    JFrame mainFrame


    private static final Logger log = LoggerFactory.getLogger(MainView.class)
    private static final PropertySaver saver = PropertySaver.getInstance()


    JTextField userid = new JTextField("", 8)
    JPasswordField pw = new JPasswordField("", 8)
    JTextField url = new JTextField("", 40)
    JTextField schema = new JTextField("", 16)
    JTextField opsHome = new JTextField("", 40)
    JTextField runId = new JTextField("", 8)
    JTextField runComment = new JTextField("", 20)
    JLabel currentSequence = new JLabel("")
    JButton exitButton = new JButton("Exit")
    JButton saveValues = new JButton("Save Values")
    JButton collectButton = new JButton("Collect Data")
    JButton buttonView = new JButton("View Data")
    JButton buttonExport = new JButton("Export Data")
    JButton buttonOpsHome = new JButton("Select Operations Home")

    JPanel messagePanel
    JLabel messageLabel

    MainView(MainController mc, MainModel mm) {
        this.mc = mc
        this.mm = mm
    }

    public static void makeLarger(JLabel field) {
        makeLarger(field, 24)
    }

    public static void makeLarger(JLabel field, int newSize) {
        Font currentFont = field.getFont()
        Font newFont = new Font(currentFont.getFontName(), Font.BOLD, newSize)
        field.setFont(newFont)
    }


    /**
     * Setup the various elements needed on the UI interface fields
     * @param modelField The String that should be in Model
     * @param viewField The textfield - will get the name of "fieldName"
     * @param fieldName the name for the field
     * Get the value from the the model field and assign it the Text of JTextField
     * Set the name value of the JTextField
     * Add this as a Documentlisterner for the JTextfield
     *
     */
    private void setupTextField(ObservableString modelField, JTextField viewField, String fieldName) {
        viewField.setText(modelField.getValue())
        viewField.setName(fieldName)
        viewField.getDocument().addDocumentListener(this)
        viewField.getDocument().putProperty("parent", viewField)
        def listener = { e ->
            if (e.oldValue.equals(e.newValue)) {
                return
            }
            if (viewField instanceof JPasswordField) {
                if (e.newValue.equals((JPasswordField) viewField.getPassword)) {
                    return
                }
            } else if (e.newValue.equals(viewField.getText())) {
                return
            }
            viewField.setText(e.newValue)
        } as PropertyChangeListener
        modelField.addPropertyChangeListener(listener)
        viewField.getDocument().addDocumentListener(this)
    }

    public void start() {
        mainFrame = new JFrame()
        mainFrame.addComponentListener(new FrameHelper())
        mainFrame.getContentPane().setLayout(new MigLayout("fillx"))
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        mainFrame.setTitle("Ops Progress Main")
        Integer frameWidth = saver.getInt("main", OpDialog.getWidthName())
        if (frameWidth == null) {
            frameWidth = 250
            saver.saveInt("main", OpDialog.getWidthName(), 250)
        }
        Integer frameHeight = saver.getInt("main", OpDialog.getHeightName())
        if (frameHeight == null) {
            frameHeight = 400
            saver.saveInt("main", OpDialog.getHeightName(), 400)
        }
        mainFrame.setSize(frameWidth, frameHeight)
        mainFrame.getContentPane().setSize(250, 400)
        mainFrame.setName("main")
        JLabel appTitle = new JLabel("Ops Progress")
        makeLarger(appTitle, 36)
        JPanel titlePanel = new JPanel(new MigLayout("center"))
        titlePanel.add(appTitle, "center, wrap")
        JLabel labelDBValues = new JLabel("Database Values")
        makeLarger(labelDBValues)
        titlePanel.add(labelDBValues, "center")
        mainFrame.getContentPane().add(titlePanel, "center, wrap")
        JLabel labelUserid = new JLabel(("Userid:"))
        JPanel contentPanel = new JPanel()
        contentPanel.setLayout(new MigLayout())
        contentPanel.add(labelUserid, "right, cell 0 1")
        userid.setToolTipText("Enter a user identifier - 1 to 8 characters, starting with a letter")
        setupTextField(mm.userid, userid, "userid")
        contentPanel.add(userid, "wrap")
        JLabel labelPassword = new JLabel("Password:")
        contentPanel.add(labelPassword, "right")
        pw.setToolTipText("Enter the password for this user (will create if this is a new database)")
        pw.setText(mm.password.getValue())
        pw.setName("password")
        pw.getDocument().addDocumentListener(this)
        pw.getDocument().putProperty("parent", pw)
        def listener = { e ->
            if (e.oldValue.equals(e.newValue)) {
                return
            }
            if (e.newValue.equals(new String(pw.getPassword()))) {
                return
            }
            mm.password.setValue(new String(pw.getPassword()))
        }
        pw.addPropertyChangeListener(listener)
        pw.getDocument().addDocumentListener(this)
        contentPanel.add(pw, "wrap")
        JLabel labelSchema = new JLabel("Schema:")
        contentPanel.add(labelSchema, "right")
        setupTextField(mm.schema, schema, "schema")
        schema.setToolTipText("Enter the schema to use in the database (default 'parser')")
        contentPanel.add(schema, "wrap")
        JLabel labelOpsHome = new JLabel("Operations Home")
        contentPanel.add(labelOpsHome, "right")
        setupTextField(mm.opsHome, opsHome, "opshome")
        opsHome.setToolTipText("Enter the location of the Operations Home direcotory (or the use the button below)")
        contentPanel.add(opsHome, "wrap")
        JLabel labelUrl = new JLabel("Database URL:")
        labelUrl.setHorizontalAlignment(SwingConstants.RIGHT)
        contentPanel.add(labelUrl, "right")
        setupTextField(mm.url, url, "url")
        url.setToolTipText("URL for the database in the form: 'jdbc:h2:file:<fullpath>")
        contentPanel.add(url, "wrap")
        JLabel labelSave = new JLabel("Press the button to save the values:")
        contentPanel.add(labelSave, "right")
        saveValues.setToolTipText("Press this button to save the values and open the database")
        saveValues.setEnabled(false)
        saveValues.addActionListener(mc.buttonSaveValuesAction)
        contentPanel.add(saveValues, "left, wrap")

        JSeparator sep1 = new JSeparator()
        sep1.setPreferredSize(new Dimension(100, 5))
        contentPanel.add(sep1, "center, span 2, wrap")
        JLabel labelRuns = new JLabel("Run Values")
        makeLarger(labelRuns)
        contentPanel.add(labelRuns, "center, span 2, wrap")
        JLabel labelRunId = new JLabel("Run Identiefier")
        contentPanel.add(labelRunId, "right")
        setupTextField(mm.runId, runId, "runid")
        runId.setToolTipText("Enter a label to identify this set of runs")
        contentPanel.add(runId, "wrap")
        JLabel labelRunComment = new JLabel("Run comment")
        contentPanel.add(labelRunComment, "right")
        setupTextField(mm.runComment, runComment, "runcomment")
        runComment.setName("runcomment")
        runComment.setToolTipText("Enter a comment about the set of runs")
        contentPanel.add(runComment, "wrap")
        JLabel labelSequence = new JLabel("Sequence")
        contentPanel.add(labelSequence, "right")
        currentSequence.setEnabled(false)
        currentSequence.setToolTipText("Disabled field showing the current run number")
        makeLarger(currentSequence)
        contentPanel.add(currentSequence, "wrap")
        collectButton.setEnabled(false)
        collectButton.setToolTipText("Press this button to read and record the OpsPro Car and Location values")
        collectButton.addActionListener(mc.buttonCollectAction)
        contentPanel.add(collectButton, "center, wrap")

        JSeparator sep2 = new JSeparator()
        sep2.setPreferredSize(new Dimension(80, 5))
        contentPanel.add(sep2, "center, span 2, wrap")
        JLabel labelViews = new JLabel("Views")
        makeLarger(labelViews)
        contentPanel.add(labelViews, "center, span 2, wrap")
        buttonView.addActionListener(mc.buttonViewAction)
        buttonView.setEnabled(false)
        contentPanel.add(buttonView, "center, wrap")
        exitButton.addActionListener(mc.buttonExitAction)
        mainFrame.getContentPane().add(contentPanel, "center, span 2,wrap")

        JSeparator sep3 = new JSeparator()
        sep3.setPreferredSize(new Dimension(100, 15))
        mainFrame.getContentPane().add(sep3, "center, span 2, wrap")
        JPanel finalPanel = new JPanel(new MigLayout("gapx 5cm"))
        finalPanel.add(exitButton)
        buttonOpsHome.addActionListener(mc.selectHomeAction)
        buttonOpsHome.setToolTipText("Press this button open a new window to choose the OpsPro Home directory")
        finalPanel.add(buttonOpsHome)
        messagePanel = new JPanel(new MigLayout("", ",fill"))
        messagePanel.setBackground(Color.WHITE)
        mm.message.setUpdateRoutine(updateMessage)
        JLabel labelForMessage = new JLabel("Message:")
        messagePanel.add(labelForMessage, "left")
        messagePanel.setPreferredSize(new Dimension(600, 20))
        messagePanel.add(mm.message.messageLabel, "grow 200, wrap")
        mainFrame.getContentPane().add(finalPanel, "center, span 2, wrap")
        mainFrame.getContentPane().add(messagePanel, "span 2, wrap")

        // now ready to display
        mainFrame.pack()
        frameWidth = mainFrame.getWidth()
        frameHeight = mainFrame.getHeight()
        Toolkit toolkit = Toolkit.getDefaultToolkit()
        int screenWidth = toolkit.getScreenSize().width
        int screenHeight = toolkit.getScreenSize().height
        Integer frameLocX = saver.getInt("main", OpDialog.getXname())
        Integer frameLocY = saver.getInt("main", OpDialog.getYname())
        if (frameLocX == null) {
            frameLocX = (screenWidth - frameWidth) / 2
            frameLocY = (screenHeight - frameHeight) / 2
            saver.saveInt("main", OpDialog.getXname(), frameLocX)
            saver.saveInt("main", OpDialog.getYname(), frameLocY)
        }
        mainFrame.setLocation(frameLocX, frameLocY)
        mm.readyToCheck = true
        mm.currentStage = MainModel.ProcessStage.CHECKING
        mm.checkFields()
        mainFrame.setVisible(true)
    }

    private void edtUpdate(Message theMessage) {
        log.debug("entered the edt update routine - message was ${theMessage}")
        String oldMessage = message.messageLabel.getText()
        log.debug("message old is ${oldMessage} and new message is ${theMessage.text}")
        message.messageLabel.setText(theMessage.text)
        switch (theMessage.msgLevel) {
            case Message.Level.ERROR:
                message.messageLabel.setBackground(Color.RED)
                message.messageLabel.setForeground(Color.WHITE)
                break
            case Message.Level.WARNING:
                message.messageLabel.setBackground(Color.YELLOW)
                message.messageLabel.setForeground(Color.BLACK)
                break
            case Message.Level.INFO:
                message.messageLabel.setBackground(Color.WHITE)
                message.messageLabel.setForeground(Color.BLACK)
                break
            default:
                log.error("msgLevel was an unknown value ${theMessage.msgLevel}")
        }
    }

    def updateMessage = { Message mess ->
        log.debug("updating message with new message ${mess.text}")
        if (SwingUtilities.isEventDispatchThread()) {
            edtUpdate(mess)
        } else {
            SwingUtilities.invokeLater {
                edtUpdate(mess)
            }
        }

    }

    private void textChangeInner(DocumentEvent e, ObservableString field, String newValue) {
        if (field.getValue().equals(newValue)) {
            return
        }
        field.setValue(newValue)
    }

    private void textChanges(DocumentEvent e) {
        JTextField theField = (JTextField) e.getDocument().getProperty("parent")
        String theValue = theField.getText()
        println("now in the TextChanges method old is ${theValue}")
        switch (theField.getName()) {
            case "userid":
                textChangeInner(e, mm.userid, theValue)
                break
            case "opshome":
                textChangeInner(e, mm.opsHome, theValue)
                break
            case "url":
                textChangeInner(e, mm.url, theValue)
                break
            case "password":
                textChangeInner(e, mm.password, theValue)
                break
            case "schema":
                textChangeInner(e, mm.schema, theValue)
                break
            case "runid":
                textChangeInner(e, mm.runId, theValue)
                break
            case "runcomment":
                textChangeInner(e, mm.runComment, theValue)
                break
            default:
                log.error("Got an unknown fieldName in switch statement ${theValue}")
        }
    }

    @Override
    void insertUpdate(DocumentEvent e) {
        textChanges(e)
    }

    @Override
    void removeUpdate(DocumentEvent e) {
        textChanges(e)
    }

    @Override
    void changedUpdate(DocumentEvent e) {
        log.error("got a Document changedUpdate event - this should not happen!", e)
    }
}
