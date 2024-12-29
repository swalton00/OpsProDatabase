package com.spw.ui

import com.spw.view.ViewDatabase
import com.spw.view.ViewCar
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JButton
import javax.swing.JComboBox
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
    JComboBox<ViewCar> carBox = new JComboBox<>()
    List<Component> rbCarList = new ArrayList<>([rbAllCars, rbMovedCars, rbSpecific, carBox])
    List<ViewCar> carList

    JRadioButton rbLocsAll = new JRadioButton("All Locations")
    JRadioButton rbLocsWith = new JRadioButton("Locations with Cars")
    JRadioButton rbLocsMoved = new JRadioButton("Cars Moved")
    JRadioButton rbLocsSpecific = new JRadioButton("Specific Locations")
    String[] locList = []
    String[] trkList = []
    JComboBox<String> locBox = new JComboBox<>(locList)
    JComboBox<String> trkBox = new JComboBox<>(trkList)
    List<Component> rbLocList = new ArrayList<>([rbLocsAll, rbLocsWith, rbLocsMoved, rbLocsSpecific, locBox, trkBox])

    JButton buttonReturn = new JButton("Close Dialog")
    JButton buttonExport = new JButton("Export Data")
    JButton buttonView = new JButton("View Data")

    void init() {
        log.debug("Select model has now been initialized")
        carList = viewdb.listCars(runId)
        carList.each {
            carBox.addItem(it)
        }
        locList = viewdb.listViewLocs(runId)
        locList.each {
            locBox.addItem(it)
        }
    }
}
