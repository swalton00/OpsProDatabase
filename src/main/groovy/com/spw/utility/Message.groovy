package com.spw.utility

import com.spw.ui.MainModel
import groovy.transform.ToString
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JLabel

@ToString(includeFields = true, includePackage = false, includeNames = true)
class Message {

    private static final Logger log = LoggerFactory.getLogger(Message.class)

    MainModel mm = null
    public enum Level {
        INFO, WARNING, ERROR
    }


    JLabel messageLable = new JLabel()
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


    }
}


