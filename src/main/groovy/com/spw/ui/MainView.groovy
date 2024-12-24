package com.spw.ui

import com.spw.utility.Message
import com.spw.utility.OpFrame
import com.spw.utility.PropertySaver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.swing.ButtonGroup
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.WindowConstants
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Toolkit
import net.miginfocom.swing.MigLayout;

class MainView {
    MainController mc
    MainModel mm

    private static final Logger log = LoggerFactory.getLogger(MainView.class)
    private static final PropertySaver saver = PropertySaver.getInstance()

    MainView(MainController mc, MainModel mm) {
        this.mc = mc
        this.mm = mm
    }

    private void makeLarger(JLabel field) {
        makeLarger(field, 24)
    }

    private void makeLarger(JLabel field, int newSize) {
        Font currentFont = field.getFont()
        Font newFont = new Font(currentFont.getFontName(), Font.BOLD, newSize)
        field.setFont(newFont)
    }

    public void start() {
        OpFrame base = new OpFrame()
        base.addComponentListener(base)
        base.getContentPane().setLayout(new MigLayout())
        base.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
        base.setTitle("Ops Progress Main")
        Integer frameWidth = saver.getInt("main", base.getWidthName())
        if (frameWidth == null) {
            frameWidth = 250
            saver.saveInt("main", base.getWidthName(), 250)
        }
        Integer frameHeight = saver.getInt("main", base.getHeightName())
        if (frameHeight == null) {
            frameHeight = 400
            saver.saveInt("main", base.getHeightName(), 400)
        }
        base.setSize(250, 400)
        base.getContentPane().setSize(250, 400)
        base.setName("main")
        JLabel appTitle = new JLabel("Ops Progress")
        makeLarger(appTitle, 36)
        JPanel titlePanel = new JPanel(new MigLayout("center"))
        titlePanel.add(appTitle, "center, wrap")
        JLabel labelDBValues = new JLabel("Database Values")
        makeLarger(labelDBValues)
        titlePanel.add(labelDBValues, "center")
        base.getContentPane().add(titlePanel, "center, wrap")
        JLabel labelUserid = new JLabel(("Userid:"))
        JPanel contentPanel = new JPanel()
        contentPanel.setLayout(new MigLayout())
        contentPanel.add(labelUserid, "right, cell 0 1")
        mm.userid.setToolTipText("Enter a user identifier - 1 to 8 characters, starting with a letter")
        mm.userid.setColumns(8)
        mm.userid.setName("userid")
        mm.userid.setColumns(8)
        contentPanel.add(mm.userid, "wrap")
        JLabel labelPassword = new JLabel("Password:")
        contentPanel.add(labelPassword, "right")
        mm.pw.setColumns(8)
        mm.pw.setName("password")
        mm.pw.setToolTipText("Enter the password for this user (will create if this is a new database)")
        contentPanel.add(mm.pw, "wrap")
        JLabel labelSchema = new JLabel("Schema:")
        contentPanel.add(labelSchema, "right")
        mm.schema.setColumns(16)
        mm.schema.setName("schema")
        mm.schema.setToolTipText("Enter the schema to use in the database (default 'parser')")
        contentPanel.add(mm.schema, "wrap")
        JLabel labelOpsHome = new JLabel("Operations Home")
        contentPanel.add(labelOpsHome, "right")
        mm.opsHome.setColumns(40)
        mm.opsHome.setName("opshome")
        mm.opsHome.setToolTipText("Enter the location of the Operations Home direcotory (or the use the button below)")
        contentPanel.add(mm.opsHome, "wrap")
        JLabel labelUrl = new JLabel("Database URL:")
        labelUrl.setHorizontalAlignment(SwingConstants.RIGHT)
        contentPanel.add(labelUrl, "right")
        mm.url.setColumns(40)
        mm.url.setName("url")
        mm.url.setToolTipText("URL for the database in the form: 'jdbc:h2:file:<fullpath>")
        contentPanel.add(mm.url, "wrap")
        JLabel labelSave = new JLabel("Press the button to save the values:")
        contentPanel.add(labelSave, "right")
        mm.saveValues.setToolTipText("Press this button to save the values and open the database")
        mm.saveValues.setEnabled(false)
        mm.saveValues.addActionListener(mc.buttonSaveValuesAction)
        contentPanel.add(mm.saveValues, "left, wrap")

        JSeparator sep1 = new JSeparator()
        sep1.setPreferredSize(new Dimension(100, 5))
        contentPanel.add(sep1, "center, span 2, wrap")
        JLabel labelRuns = new JLabel("Run Values")
        makeLarger(labelRuns)
        contentPanel.add(labelRuns, "center, span 2, wrap")
        JLabel labelRunId = new JLabel("Run Identiefier")
        contentPanel.add(labelRunId, "right")
        mm.runId.setColumns(8)
        mm.runId.setName("runid")
        mm.runId.setToolTipText("Enter a label to identify this set of runs")
        contentPanel.add(mm.runId, "wrap")
        JLabel labelRunComment = new JLabel("Run comment")
        contentPanel.add(labelRunComment, "right")
        mm.runComment.setColumns(20)
        mm.runComment.setName("runcomment")
        mm.runComment.setToolTipText("Enter a comment about the set of runs")
        contentPanel.add(mm.runComment, "wrap")
        JLabel labelSequence = new JLabel("Sequence")
        contentPanel.add(labelSequence, "right")
        mm.currentSequence.setEnabled(false)
        mm.currentSequence.setToolTipText("Disabled field showing the current run number")
        makeLarger(mm.currentSequence)
        contentPanel.add(mm.currentSequence, "wrap")
        mm.collectButton.setEnabled(false)
        mm.collectButton.setToolTipText("Press this button to read and record the OpsPro Car and Location values")
        mm.collectButton.addActionListener(mc.buttonCollectAction)
        contentPanel.add(mm.collectButton, "center, wrap")

        JSeparator sep2 = new JSeparator()
        sep2.setPreferredSize(new Dimension(80, 5))
        contentPanel.add(sep2, "center, span 2, wrap")
        JLabel labelViews = new JLabel("Views")
        makeLarger(labelViews)
        contentPanel.add(labelViews, "center, span 2, wrap")
        ButtonGroup viewGroup = new ButtonGroup()
        viewGroup.add(mm.radioCarByLoc)
        JPanel viewPanel = new JPanel(new MigLayout())
        mm.radioCarByLoc.setSelected(true)
        mm.radioLocByCar.addActionListener(mc.radioAction)
        mm.radioCarByLoc.addActionListener(mc.radioAction)
        viewGroup.add(mm.radioLocByCar)
        viewPanel.add(mm.radioCarByLoc)
        viewPanel.add(mm.radioLocByCar)
        mm.buttonView.addActionListener(mc.buttonViewAction)
        mm.buttonView.setEnabled(false)
        viewPanel.add(mm.buttonView)
        mm.buttonExport.addActionListener(mc.buttonExportAction)
        mm.buttonExport.setEnabled(false)
        viewPanel.add(mm.buttonExport)
        contentPanel.add(viewPanel, "center, span 2, wrap")
        mm.exitButton.addActionListener(mc.buttonExitAction)
        base.getContentPane().add(contentPanel, "center, span 2,wrap")

        JSeparator sep3 = new JSeparator()
        sep3.setPreferredSize(new Dimension(100, 15))
        base.getContentPane().add(sep3, "center, span 2, wrap")
        JPanel finalPanel = new JPanel(new MigLayout("gapx 5cm"))
        finalPanel.add(mm.exitButton)
        mm.buttonOpsHome.addActionListener(mc.selectHomeAction)
        mm.buttonOpsHome.setToolTipText("Press this button open a new window to choose the OpsPro Home directory")
        finalPanel.add(mm.buttonOpsHome)
        mm.messagePanel = new JPanel(new MigLayout("", ",fill"))
        mm.messagePanel.setBackground(Color.WHITE)
        mm.message.setUpdateRoutine(updateMessage)
        JLabel labelForMessage = new JLabel("Message:")
        mm.messagePanel.add(labelForMessage, "left")
        mm.messagePanel.setPreferredSize(new Dimension(600, 20))
        mm.message.messageLabel.setColumns(400)
        mm.messagePanel.add(mm.message.messageLabel, "grow 200, wrap")
        base.getContentPane().add(finalPanel, "center, span 2, wrap")
        base.getContentPane().add(mm.messagePanel, "span 2, wrap")

        // now ready to display
        base.pack()
        frameWidth = base.getWidth()
        frameHeight = base.getHeight()
        Toolkit toolkit = Toolkit.getDefaultToolkit()
        int screenWidth = toolkit.getScreenSize().width
        int screenHeight = toolkit.getScreenSize().height
        Integer frameLocX = saver.getInt("main", base.getXname())
        Integer frameLocY = saver.getInt("main", base.getYname())
        if (frameLocX == null) {
            frameLocX = (screenWidth - frameWidth) / 2
            frameLocY = (screenHeight - frameHeight) / 2
            saver.saveInt("main", base.getXname(), frameLocX)
            saver.saveInt("main", base.getYname(), frameLocY)
        }
        base.setLocation(frameLocX, frameLocY)
        mm.readyToCheck = true
        mm.currentStage = MainModel.ProcessStage.CHECKING
        mm.checkFields()
        base.setVisible(true)
    }

    private void edtUpdate(Message theMessage) {
        log.debug("entered the edt update routine - message was ${theMessage}")
        String oldMessage = mm.message.messageLabel.getText()
        log.debug("message old is ${oldMessage} and new message is ${theMessage.text}")
        mm.message.messageLabel.setText(theMessage.text)
        switch (theMessage.msgLevel) {
            case Message.Level.ERROR:
                mm.message.messageLabel.setBackground(Color.RED)
                mm.message.messageLabel.setForeground(Color.WHITE)
                break
            case Message.Level.WARNING:
                mm.message.messageLabel.setBackground(Color.YELLOW)
                mm.message.messageLabel.setForeground(Color.BLACK)
                break
            case Message.Level.INFO:
                mm.message.messageLabel.setBackground(Color.WHITE)
                mm.message.messageLabel.setForeground(Color.BLACK)
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
}
