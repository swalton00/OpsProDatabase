package com.spw.view

import com.spw.utility.OpDialog
import com.spw.utility.PropertySaver
import net.miginfocom.swing.MigLayout

import javax.swing.JDialog
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JScrollPane
import javax.swing.JTable
import java.awt.event.KeyEvent

class ViewTableView {

    OpDialog parent
    ViewTableModel model
    ViewTableController controller
    PropertySaver saver = PropertySaver.getInstance()
    JDialog thisDialog
    JTable theTable


    public void init() {
        thisDialog = new OpDialog(parent, "Car Movement Data", true)
        thisDialog.setName("table")
        thisDialog.setLayout(new MigLayout())
        Integer dialogWidth = saver.getInt("table", thisDialog.getWidthName())
        if (dialogWidth == null) {
            dialogWidth = 1000
            saver.saveInt("table", thisDialog.getWidthName(), dialogWidth)
        }
        Integer dialogHeight = saver.getInt("table", thisDialog.getHeightName())
        if (dialogHeight == null) {
            dialogHeight = 1200
            saver.saveInt("table", thisDialog.getHeightName(), dialogHeight)
        }
        thisDialog.setSize(dialogWidth, dialogHeight)

        JMenuBar menuBar = new JMenuBar()
        JMenu fileMenu = new JMenu("File")
        menuBar.add(fileMenu)
        JMenuItem fileClose = new JMenuItem("Close", KeyEvent.VK_C)
        menuBar.add(fileClose)
        fileClose.addActionListener(controller.closeAction)
        thisDialog.getContentPane().add(menuBar)

        theTable = new JTable(model.dataForTable, model.columnHeader)
        JScrollPane tableScroll = new JScrollPane(theTable)
        thisDialog.add(tableScroll)
        thisDialog.pack()
        thisDialog.setVisible(true)
    }
}
