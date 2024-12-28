package com.spw.ui

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JRadioButton

class SelectModel {
    private static final Logger log = LoggerFactory.getLogger(SelectModel.class)

    JRadioButton radioCarByLoc = new JRadioButton("By Car")
    JRadioButton radioLocByCar = new JRadioButton("By Location")

    JRadioButton rbAllCars = new JRadioButton("All cars")
    JRadioButton rbMovedCars = new JRadioButton("Moved")
    JRadioButton rbSpecific = new JRadioButton(("Specific cars"))
    List<JRadioButton> rbCarList = new ArrayList<>([rbAllCars, rbMovedCars, rbSpecific])


    String[] carList = ["PRR123", "ARLX123456"]

    JComboBox<String> carBox = new JComboBox<>(carList)

    JRadioButton rbLocsAll = new JRadioButton("All Locations")
    JRadioButton rbLocsWith = new JRadioButton("Locations with Cars")
    JRadioButton rbLocsMoved = new JRadioButton("Cars Moved")
    JRadioButton rbLocsSpecific = new JRadioButton("Specific Locations")
    List<JRadioButton> rbLocList = new ArrayList<>([rbLocsAll, rbLocsWith, rbLocsMoved, rbLocsSpecific])

    String[] locList = ["Earls Energy", "The Yard"]
    String[] trkList = ["Track 1", "Track 2"]
    JComboBox<String> locBox = new JComboBox<>(locList)
    JComboBox<String> trkBox = new JComboBox<>(trkList)

    JButton buttonReturn = new JButton("Close Dialog")
    JButton buttonExport = new JButton("Export Data")
    JButton buttonView = new JButton("View Data")

    void init() {
        log.debug("Select model has now been initialized")
    }
}
