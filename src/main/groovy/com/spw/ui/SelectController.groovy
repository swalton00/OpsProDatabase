package com.spw.ui


import com.spw.utility.OpDialog
import com.spw.utility.RunTasks
import com.spw.view.RowElement
import com.spw.view.ViewCar
import com.spw.view.ViewDatabase
import com.spw.view.ViewElement
import com.spw.view.ViewLoc
import com.spw.view.ViewParameter
import com.spw.view.ViewTableController
import com.spw.view.ViewTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JDialog
import javax.swing.JRadioButton
import javax.swing.event.ListSelectionEvent
import java.awt.Window
import java.awt.event.ActionEvent

class SelectController {

    private static final Logger log = LoggerFactory.getLogger(SelectController.class)

    private static final ViewDatabase vdb = ViewDatabase.getInstance()

    String runId

    SelectView sv
    SelectModel sm
    Window parent
    ViewParameter parameters
    RunTasks runner = RunTasks.getInstance()
    ViewTableController tableController

    /**
     * Create a new Select Controller to setup for viewing data
     * @param runId     the identifier in the database for this data
     * @param parentFrame the parent of this frame
     */
    SelectController(String runId, Window parentFrame) {
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

    def returnAction = { ActionEvent e ->
        log.debug("got a return request")
        sv.viewDialog.setVisible(false)
    }

    def exportAction = { ActionEvent e ->
        log.debug("got an export request")
    }

    def viewActionBackground = { ->
        tableController = new ViewTableController(runId, sv.viewDialog, parameters )
        tableController.model.modelParameter = parameters
         //               runId, sv.viewDialog, parameters
        // create title line, get rows for view, populate rows
        tableController.model.columnHeader = new Vector<>()
        List<Integer> seqs = vdb.getSequences(runId)
        List<RowElement> rowData
        if (parameters.runType.equals(ViewElement.RunType.CAR)) {
            ViewElement.setType(ViewElement.RunType.CAR, true, true)
            tableController.model.columnHeader.add("Car Number")
            rowData = vdb.getRowElements(parameters)
        } else {
            ViewElement.setType(ViewElement.RunType.TRACK, true, true)
            tableController.model.columnHeader.add("Track Name")
            rowData = vdb.getRowLocs(parameters)
        }
        seqs.each {
            tableController.model.columnHeader.add(it.toString())
        }
        tableController.model.dataForTable = new Vector<>()
        rowData.each {row ->
            Vector<Object> currentRow = new Vector<>()
            currentRow.add(row)
            seqs.each {seq ->
                row.elements.each {element ->
                    if (element.sequenceNumber.equals(seq)) {
                        currentRow.add(element)
                    }
                }
            }
            tableController.model.dataForTable.add(currentRow)
        }
        tableController.init()
    }

    def viewAction = { ActionEvent ->
        log.debug("got a view request")
        //first capture the options,
        //  next switch to background thread
        //  build the relevant parts - titles, rows
        // create the viewTable classes and pass control
        parameters = new ViewParameter()
        parameters.runId = runId
        if (sm.radioCarByLoc.isSelected()) {
            parameters.runType = ViewElement.RunType.CAR
            if (sm.rbAllCars.isSelected()) {
                parameters.carSelect = ViewParameter.CarSelect.ALL
            } else if (sm.rbMovedCars.isSelected()) {
                parameters.carSelect = ViewParameter.CarSelect.MOVED
            } else if (sm.rbSpecific.isSelected()) {
                parameters.carSelect = ViewParameter.CarSelect.SPECIFIC
                List<ViewCar> selCars = sm.carBox.getSelectedValuesList()
                parameters.idList = new ArrayList<>()
                selCars.each {
                    parameters.idList.add(it.carId)
                }
            } else {
                log.error("no selection amoung radio buttons for Cars")
            }
        } else {
            parameters.runType = ViewElement.RunType.TRACK
            if (sm.rbLocsAll.isSelected()) {
                parameters.locSelect = ViewParameter.LocSelect.ALL
            } else if (sm.rbLocsWith.isSelected()) {
                parameters.locSelect = ViewParameter.LocSelect.WITH
            } else if (sm.rbLocsMoved.isSelected()) {
                parameters.locSelect = ViewParameter.LocSelect.MOVED
            } else if (sm.rbLocsSpecific.isSelected()) {
                parameters.locSelect = ViewParameter.LocSelect.SPECIFIC
                parameters.idList = new ArrayList<>()
                boolean onlyLocs = false
                if (sm.trkBox.isSelectionEmpty()) {
                    // no tracks selected - use all from location
                    List<ViewLoc> selLocs = sm.locBox.getSelectedValuesList()
                    selLocs.each { loc ->
                        loc.tracks.each { trk ->
                            parameters.idList.add(trk.trkId)
                        }
                    }
                } else {
                    // use only selected tracks
                    List<ViewTrack> trks = sm.trkBox.getSelectedValuesList()
                    trks.each {
                        parameters.idList.add(it.trkId)
                    }
                }
            } else {
                log.error("no selection amoung radio buttons for Location type")
            }
        }
        runner.runIt(viewActionBackground)
    }

    def selectionAction = { ListSelectionEvent e ->
        log.trace("Got a selection event on the combo box - ${e}")
        if (e.getValueIsAdjusting()) {
            // hasn't stabilized yet - skip
            return
        }
        List<ViewLoc> selectedList = sm.locBox.getSelectedValuesList()
        sm.trkBoxModel.removeAllElements()
        selectedList.each {
            sm.trkBoxModel.addAll(it.tracks)
        }

    }

    public void init() {
        sm = new SelectModel(runId)
        sm.init()
        sv = new SelectView(sm, this, parent)
        sv.init()
    }


}
