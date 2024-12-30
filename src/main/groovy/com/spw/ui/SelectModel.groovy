package com.spw.ui

import com.spw.view.ViewDatabase
import com.spw.view.ViewCar
import com.spw.view.ViewLoc
import com.spw.view.ViewTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JList
import javax.swing.JRadioButton
import java.awt.Component

class SelectModel {

    SelectModel(String runId) {
        this.runId = runId
    }

    private static ViewDatabase viewdb = ViewDatabase.getInstance()
    String runId
    private static final Logger log = LoggerFactory.getLogger(SelectModel.class)

    JRadioButton radioCarByLoc = new JRadioButton("By Car")
    JRadioButton radioLocByCar = new JRadioButton("By Location")

    JRadioButton rbAllCars = new JRadioButton("All cars")
    JRadioButton rbMovedCars = new JRadioButton("Moved")
    JRadioButton rbSpecific = new JRadioButton(("Specific cars"))
    Vector<ViewCar> carList = new Vector<>()
    JList<ViewCar> carBox
    List<Component> rbCarList = new ArrayList<>([rbAllCars, rbMovedCars, rbSpecific])

    JRadioButton rbLocsAll = new JRadioButton("All Locations")
    JRadioButton rbLocsWith = new JRadioButton("Locations with Cars")
    JRadioButton rbLocsMoved = new JRadioButton("Cars Moved")
    JRadioButton rbLocsSpecific = new JRadioButton("Specific Locations")
    Vector<ViewLoc> locList = new Vector()
    Vector<ViewTrack> trkList = new Vector()
    JList<ViewLoc> locBox
    JList<ViewTrack> trkBox
    DefaultListModel<ViewTrack> trkBoxModel
    List<Component> rbLocList = new ArrayList<>([rbLocsAll, rbLocsWith, rbLocsMoved, rbLocsSpecific])

    JButton buttonReturn = new JButton("Close Dialog")
    JButton buttonExport = new JButton("Export Data")
    JButton buttonView = new JButton("View Data")

    void init() {
        log.debug("Select model has now been initialized")
        List<ViewCar> retList  = viewdb.listCars(runId)
        carList.addAll(retList)
        carBox = new JList<ViewCar>(carList)
        List<ViewLoc> retLocs = viewdb.listViewLocs(runId)
        locList.addAll(retLocs)
        locBox = new JList<ViewLoc>(locList)
        trkBoxModel = new DefaultListModel<>()
        trkBox = new JList<>(trkBoxModel)
        trkBox.setPrototypeCellValue("this is track 1234")
        rbCarList.add(carBox)
        rbLocList.add(locBox)
        rbLocList.add(trkBox)
    }
}
