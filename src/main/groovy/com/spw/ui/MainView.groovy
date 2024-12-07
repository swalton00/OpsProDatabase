package com.spw.ui

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField
import javax.swing.SwingConstants
import javax.swing.WindowConstants
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
        contentPanel.add(labelUserid, "cell 0 1")
        mm.userid.horizontalAlignment = SwingConstants.LEFT
        mm.userid.setColumns(8)
        contentPanel.add(mm.userid, "wrap")
        JLabel labelPassword = new JLabel("Password:")
        labelPassword.horizontalAlignment = SwingConstants.RIGHT
        contentPanel.add(labelPassword)
        mm.pw.setColumns(8)
        mm.pw.horizontalAlignment = SwingConstants.LEFT
        mm.userid.setColumns(8)
        contentPanel.add(mm.pw, "wrap")
        JLabel labelSchema = new JLabel("Schema:")
        labelSchema.setHorizontalAlignment(SwingConstants.RIGHT)
        contentPanel.add(labelSchema)
        mm.schema.setColumns(16)
        contentPanel.add(mm.schema, "wrap")
        JLabel labelUrl = new JLabel("Database URL:")
        labelUrl.setHorizontalAlignment(SwingConstants.RIGHT)
        contentPanel.add(labelUrl)
        mm.url.setColumns(80)
        contentPanel.add(mm.url,"wrap")
        base.getContentPane().add(contentPanel)
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
