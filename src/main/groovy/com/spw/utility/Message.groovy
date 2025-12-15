package com.spw.utility

import com.spw.ui.MainModel
import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JLabel
import javax.swing.JTextField

@ToString(includePackage = false, includeNames = true, includes = ["text", "msgLevel"])
class Message {

    private static final Logger log = LoggerFactory.getLogger(Message.class)

    MainModel mm = null
    public enum Level {
        INFO, WARNING, ERROR
    }

    String text = ""
    JTextField messageLabel = new JTextField(" ", 400)
    Level msgLevel

    Message() {

    }

    def updateRoutine

    void setUpdateRoutine(Closure updateRoutine) {
        this.updateRoutine = updateRoutine
    }

    Message(MainModel mainModel) {
        mm = mainModel
    }

    void setText(String text, Level msgLevel) {
        this.text = text
        this.msgLevel = msgLevel
        if (updateRoutine != null) {
            updateRoutine(this)
        }

    }
}


