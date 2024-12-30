package com.spw.ui

import com.spw.rr.DatabaseProcess
import com.spw.utility.OpDialog
import com.spw.view.ViewLoc
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JRadioButton
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent

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

    def radioListener = { ActionEvent e ->
        if (e.getSource().getClass() == JRadioButton.class) {
            boolean isCars = false
            if (e.getSource() == sm.radioCarByLoc) {
                isCars = true
            } else {
                isCars = false
            }
            sm.rbCarList.each {
                it.setEnabled(isCars)
            }
            sm.rbLocList.each {
                it.setEnabled(!isCars)
            }
        }
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

    def selectionAction  = { ItemEvent e ->
        log.trace("Got a selection event on the combo box - ${e}")
        if (e.getStateChange() == ItemEvent.SELECTED) {
            // new Location was selected - add associated tracks
            int selected = sm.locBox.getSelectedIndex()
            ViewLoc thisItem = sm.locList.getAt(selected)
            thisItem.tracks.each {
                sm.trkBox.addItem(it)
            }
        } else {
            // Location item was DEselected - remove all tracks
            sm.trkBox.removeAllItems()
        }
    }

    public void init() {
        sm = new SelectModel(runId)
        sm.init()
        sv = new SelectView(sm, this, parent)
        sv.init()
    }


}
