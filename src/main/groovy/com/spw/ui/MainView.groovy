package com.spw.ui

import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPasswordField
import javax.swing.SwingConstants
import javax.swing.WindowConstants
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Toolkit

class MainView {
    MainController mc
    MainModel mm

    MainView(MainController mc, MainModel mm) {
        this.mc = mc
        this.mm = mm
    }

    public void start() {
        JFrame base = new JFrame()
        base.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
        base.setTitle("OpsPress Main")
        base.setSize(120, 250)
        int frameWidth = base.getWidth()
        int frameHeight = base.getHeight()
        Toolkit toolkit = Toolkit.getDefaultToolkit()
        int screenWidth = toolkit.getScreenSize().width
        int screenHeight = toolkit.getScreenSize().height
        int x = (screenWidth - frameWidth)/2
        int y = (screenHeight - frameHeight)/2
        base.setLocation(x, y)
        base.setLayout(new GridBagLayout())

        JLabel appTitle = new JLabel("Ops Progress", SwingConstants.CENTER)
        GridBagConstraints gbc = new GridBagConstraints()
        gbc.fill = GridBagConstraints.HORIZONTAL
        gbc.gridx = 0
        gbc.gridy = 0
        base.getContentPane().add(appTitle, gbc)
        JLabel userid = new JLabel(("Userid:"))
        gbc.gridy = 1
        userid.horizontalAlignment = SwingConstants.RIGHT
        gbc.weightx = 0.5
        base.getContentPane().add(userid, gbc)
        gbc.gridx = 1
        mm.userid.horizontalAlignment = SwingConstants.LEFT
        base.getContentPane().add(mm.userid, gbc)
        gbc.gridx = 0
        gbc.gridy = 2
        JLabel password = new JLabel("Password:")
        password.horizontalAlignment = SwingConstants.RIGHT
        base.getContentPane().add(password, gbc)
        mm.pw.horizontalAlignment = SwingConstants.LEFT
        gbc.gridx = 1
        base.getContentPane().add(mm.pw, gbc)
        base.pack()
        base.setVisible(true)

    }
}
