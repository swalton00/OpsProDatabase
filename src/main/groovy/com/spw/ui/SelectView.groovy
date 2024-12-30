package com.spw.ui

import com.spw.utility.OpDialog
import com.spw.utility.PropertySaver
import com.spw.view.ViewCar
import com.spw.view.ViewLoc
import com.spw.view.ViewTrack
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.DefaultListModel
import javax.swing.JDialog
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import net.miginfocom.swing.MigLayout

import javax.swing.JRadioButton
import javax.swing.JScrollBar
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import javax.swing.border.Border
import java.awt.Dialog
import java.awt.Dimension
import java.awt.Toolkit;

class SelectView {

    private static final Logger log = LoggerFactory.getLogger(SelectView.class)

    SelectModel sm
    SelectController sc

    PropertySaver saver = PropertySaver.getInstance()

    JDialog viewDialog
    Dialog parent


    /**
     * Create the view for the selection of data for display
     * @param selectModel       the model for the data
     * @param selectController  the controller to handle all the actions
     * @param parent            the parent frome (to make it modal)
     */
    SelectView(SelectModel selectModel, SelectController selectController, JDialog parent) {
        this.sm = selectModel
        this.sc = selectController
        this.parent = parent
    }

    public void init() {
        viewDialog = new OpDialog(parent, "Choose View Options", true )
        viewDialog.setName("view")
        viewDialog.setLayout(new MigLayout("fillx"))
        Integer frameWidth = saver.getInt("view", viewDialog.getWidthName())
        if (frameWidth == null) {
            frameWidth = 275
            saver.saveInt("view", viewDialog.getWidthName(), frameWidth)
        }
        Integer frameHeight = saver.getInt("view", viewDialog.getHeightName())
        if (frameHeight == null) {
            frameHeight = 400
            saver.saveInt("view", viewDialog.getHeightName(), frameHeight)
        }
        viewDialog.setSize(frameWidth, frameHeight)

        ButtonGroup viewGroup = new ButtonGroup()
        viewGroup.add(sm.radioCarByLoc)
        sm.radioCarByLoc.setName("rbCars")
        JPanel viewPanel = new JPanel(new MigLayout())
        sm.radioLocByCar.addActionListener(sc.radioListener)
        sm.radioCarByLoc.setSelected(true)
        sm.radioCarByLoc.addActionListener(sc.radioListener)
        sm.radioLocByCar.setName("rbLocs")
        viewGroup.add(sm.radioLocByCar)
        viewPanel.add(sm.radioCarByLoc)
        viewPanel.add(sm.radioLocByCar)
        Border mainBorder = BorderFactory.createTitledBorder("Organized By")
        viewPanel.setBorder(mainBorder)
        viewDialog.add(viewPanel, "span 3, wrap")

        JPanel carsPanel = new JPanel(new MigLayout())
        carsPanel.add(sm.rbAllCars, "left")
        sm.rbAllCars.setSelected(true)
        JLabel labelAllCars = new JLabel("This option select ALL the cars")
        carsPanel.add(labelAllCars, "wrap")
        carsPanel.add(sm.rbMovedCars, "left")
        JLabel labelFullMoved = new JLabel("Only cars that moved")
        carsPanel.add(labelFullMoved, "left, wrap")
        carsPanel.add(sm.rbSpecific, "left")
        ButtonGroup carsGroup = new ButtonGroup()
        carsGroup.add(sm.rbAllCars)
        carsGroup.add(sm.rbMovedCars)
        carsGroup.add(sm.rbSpecific)
        JScrollPane carBoxPane = new JScrollPane(sm.carBox)
        sm.carBox.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        sm.carBox.setLayoutOrientation(JList.VERTICAL)
        sm.carBox.setVisibleRowCount(5)
        sm.carBox.setPreferredSize(new Dimension(50, 75))
        carBoxPane.setPreferredSize(new Dimension(100, 75))
        carsPanel.add(carBoxPane, "wrap")
        sm.rbAllCars.setSelected(true)

        Border carBorder = BorderFactory.createTitledBorder("Car options")
        carsPanel.setBorder(carBorder)
        viewDialog.add(carsPanel, "Wrap")

        JPanel locsPanel = new JPanel(new MigLayout())
        ButtonGroup locGroup = new ButtonGroup()
        locGroup.add(sm.rbLocsAll)
        locGroup.add(sm.rbLocsWith)
        locGroup.add(sm.rbLocsMoved)
        locGroup.add(sm.rbLocsSpecific)
        locsPanel.add(sm.rbLocsAll, "wrap")
        sm.rbLocsAll.setSelected(true)
        locsPanel.add(sm.rbLocsWith, "wrap")
        locsPanel.add(sm.rbLocsMoved, "wrap")
        locsPanel.add(sm.rbLocsSpecific)
        sm.locBox.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        sm.locBox.setLayoutOrientation(JList.VERTICAL)
        sm.locBox.setVisibleRowCount(5)
        sm.locBox.setPreferredSize(new Dimension(100, 100))
        JScrollPane locBoxPane = new JScrollPane(sm.locBox)
        locBoxPane.setPreferredSize(new Dimension(150, 100))
        locsPanel.add(locBoxPane)
        JScrollPane trkBoxPane = new JScrollPane(sm.trkBox)
        sm.trkBox.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        sm.trkBox.setLayoutOrientation(JList.VERTICAL)
        sm.trkBox.setVisibleRowCount(5)
        trkBoxPane.setPreferredSize(new Dimension( 150, 100))
        locsPanel.add(trkBoxPane, "wrap")
        sm.rbLocList.each {
            it.setEnabled(false)
        }
        Border locBorder = BorderFactory.createTitledBorder("Location Options:")
        locsPanel.setBorder(locBorder)
        viewDialog.add(locsPanel, "wrap")

        viewDialog.add(sm.buttonReturn)
        sm.buttonReturn.addActionListener(sc.returnAction)
        sm.buttonExport.addActionListener(sc.exportAction)
        sm.buttonView.addActionListener(sc.viewAction)
        viewDialog.add(sm.buttonExport, "center")
        viewDialog.add(sm.buttonView, "wrap")

        frameHeight = viewDialog.getHeight()
        frameWidth = viewDialog.getWidth()
        Toolkit toolkit = Toolkit.getDefaultToolkit()
        int screenWidth = toolkit.getScreenSize().getWidth()
        int screenHeight = toolkit.getScreenSize().getHeight()
        Integer locX = saver.getInt("view", viewDialog.getXname())
        Integer locY = saver.getInt("view", viewDialog.getYname())
        if (locX == null) {
            locX = (screenWidth - frameWidth) / 2
            locY = (screenHeight - frameHeight) / 2
            saver.saveInt("view", viewDialog.getXname(), locX)
            saver.saveInt("view", viewDialog.getYname(), locY)
        }
        viewDialog.setLocation(locX, locY)
        viewDialog.pack()
        viewDialog.setVisible(true)

    }
}
