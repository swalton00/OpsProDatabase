package com.spw.ui

import groovy.transform.ToString

import javax.swing.JPasswordField
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

}
