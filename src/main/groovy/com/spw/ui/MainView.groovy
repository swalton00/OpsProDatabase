package com.spw.ui

import javax.swing.ButtonGroup
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import java.awt.Dimension
import java.awt.Toolkit
import net.miginfocom.swing.MigLayout;

class MainView {
    MainController mc
    MainModel mm

    MainView(MainController mc, MainModel mm) {
        this.mc = mc
        this.mm = mm
    }

    public void start() {
        JFrame base = new JFrame()
        base.getContentPane().setLayout(new MigLayout())
        base.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        base.setTitle("OpsPress Main")
        base.setSize(250, 400)
        base.getContentPane().setSize(250, 400)
        JLabel appTitle = new JLabel("Ops Progress")
        appTitle.setHorizontalAlignment( SwingConstants.CENTER)
        JPanel titlePanel = new JPanel()
        titlePanel.add(appTitle)
        base.getContentPane().add(titlePanel, "center, wrap")
        JLabel labelUserid = new JLabel(("Userid:"))
        JPanel contentPanel = new JPanel()
        contentPanel.setLayout(new MigLayout())
        labelUserid.horizontalAlignment = SwingConstants.RIGHT
        contentPanel.add(labelUserid, "right, cell 0 1")
        mm.userid.horizontalAlignment = SwingConstants.LEFT
        mm.userid.setColumns(8)
        contentPanel.add(mm.userid, "wrap")
        JLabel labelPassword = new JLabel("Password:")
        labelPassword.horizontalAlignment = SwingConstants.RIGHT
        contentPanel.add(labelPassword, "right")
        mm.pw.setColumns(8)
        mm.pw.horizontalAlignment = SwingConstants.LEFT
        mm.userid.setColumns(8)
        contentPanel.add(mm.pw, "wrap")
        JLabel labelSchema = new JLabel("Schema:")
        contentPanel.add(labelSchema, "right")
        mm.schema.setColumns(16)
        contentPanel.add(mm.schema, "wrap")
        JLabel labelOpsHome = new JLabel("Operations Home")
        contentPanel.add(labelOpsHome, "right")
        mm.opsHome.setColumns(20)
        contentPanel.add(mm.opsHome, "wrap")
        JLabel labelUrl = new JLabel("Database URL:")
        labelUrl.setHorizontalAlignment(SwingConstants.RIGHT)
        contentPanel.add(labelUrl, "right")
        mm.url.setColumns(40)
        contentPanel.add(mm.url,"wrap")
        JLabel labelSave = new JLabel("Press the button to save the values:")
        contentPanel.add(labelSave, "right")
        contentPanel.add(mm.saveValues, "left, wrap")
        JSeparator sep1 = new JSeparator()
        sep1.setPreferredSize(new Dimension(100, 5))
        contentPanel.add(sep1, "center, span 2, wrap")
        JLabel labelRuns = new JLabel("Run Values")
        contentPanel.add(labelRuns, "center, span 2, wrap")
        JLabel labelRunId = new JLabel("Run Identiefier")
        contentPanel.add(labelRunId, "right")
        mm.runId.setColumns(8)
        mm.runId.setEnabled(false)
        contentPanel.add(mm.runId, "wrap")
        JLabel labelRunComment = new JLabel("Run comment")
        contentPanel.add(labelRunComment, "right")
        mm.runComment.setColumns(20)
        contentPanel.add(mm.runComment, "wrap")
        JLabel labelSequence = new JLabel("Sequence")
        contentPanel.add(labelSequence, "right")
        mm.currentSequence.setEnabled(false)
        mm.currentSequence.setColumns(8)
        contentPanel.add(mm.currentSequence, "wrap")
        JSeparator sep2 = new JSeparator()
        sep2.setPreferredSize(new Dimension(80, 5))
        contentPanel.add(sep2, "center, span 2, wrap")
        JLabel labelViews = new JLabel("Views")
        contentPanel.add(labelViews, "center, span 2, wrap")
        ButtonGroup viewGroup = new ButtonGroup()
        viewGroup.add(mm.radioCarByLoc)
        JPanel viewPanel = new JPanel(new MigLayout())
        mm.radioLocByCar.addActionListener(mc.radioAction)
        mm.radioCarByLoc.addActionListener(mc.radioAction)
        viewGroup.add(mm.radioLocByCar)
        viewPanel.add(mm.radioCarByLoc)
        viewPanel.add(mm.radioLocByCar)
        mm.buttonView.addActionListener(mc.buttonViewAction)
        viewPanel.add(mm.buttonView)
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
        finalPanel.add(mm.buttonOpsHome)
        base.getContentPane().add(finalPanel, "center, span 2, wrap")

        // now ready to display
        base.pack()
        int frameWidth = base.getWidth()
        int frameHeight = base.getHeight()
        Toolkit toolkit = Toolkit.getDefaultToolkit()
        int screenWidth = toolkit.getScreenSize().width
        int screenHeight = toolkit.getScreenSize().height
        int x = (screenWidth - frameWidth)/2
        int y = (screenHeight - frameHeight)/2
        base.setLocation(x, y)

        base.setVisible(true)

    }
}
