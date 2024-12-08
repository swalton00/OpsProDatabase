package com.spw.ui

import groovy.transform.ToString

import javax.swing.JButton
import javax.swing.JPasswordField
import javax.swing.JRadioButton
import javax.swing.JTextField

@ToString(includeNames = true, includePackage = false, includeFields = true)
class MainModel {

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

}
