package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.utility.OpDialog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JDialog
import javax.swing.JFrame

class SelectController {

    private static final Logger log = LoggerFactory.getLogger(SelectController.class)

    private static final DatabaseProcess db = DatabaseProcess.getInstance()

    String runId

    SelectView sv
    SelectModel sm
    JDialog parent

    /**
     * Create a new Select Controller to setup for viewing data
     * @param userid    the userid for db access
     * @param password  the password for that user
     * @param url       the URL for the database
     * @param schema    the schema to add to the url
     * @param parentFrame   the dparent of this frame
     */
    SelectController(String runId, OpDialog parentFrame) {
        log.debug("creating a Select controller")
        this.runId = runId
        this.parent = parentFrame
    }

    def returnAction  = { ActionEvent ->
        log.debug("got a return request")
        sv.viewDialog.setVisible(false)
    }

    def exportAction  = { ActionEvent ->
        log.debug("got an export request")
    }

    def viewAction  = { ActionEvent ->
        log.debug("got a view request")
    }

    public void init() {
        sm = new SelectModel(runId)
        sm.init()
        sv = new SelectView(sm, this, parent)
        sv.init()
    }


}
