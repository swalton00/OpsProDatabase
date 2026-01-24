package com.spw.view

import com.spw.mappers.RunId
import com.spw.utility.OpDialog
import com.spw.utility.PropertySaver
import net.miginfocom.swing.MigLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.ButtonGroup
import javax.swing.JDialog
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JRadioButton
import javax.swing.JRadioButtonMenuItem
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumn
import javax.swing.table.TableColumnModel
import javax.swing.table.TableModel
import javax.swing.table.TableRowSorter
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.awt.Toolkit
import java.awt.event.KeyEvent

class ViewTableView {

    private static final Logger log = LoggerFactory.getLogger(ViewTableView.class)

    OpDialog parent
    ViewTableModel model
    ViewTableController controller
    PropertySaver saver = PropertySaver.getInstance()
    JDialog thisDialog
    JTable theTable
    JRadioButtonMenuItem byReporting
    JRadioButtonMenuItem byType

    private ArrayList<ArrayList<Dimension>> calculateCellMax() {
        log.debug("calculating max values for height of each cell ")
        /**
         * fill an array of arrays of Widths(width, height) for each cell
         *      call the renderer with for each cell, get size of resulting JLabel
         *      Fill an array for the row, one for each column
         *      add that to array of rows
         * Then, build an arroy of max widths (one for each column)
         *      and build an array of maxHeights (on for each row)
         */
        ArrayList<ArrayList<Dimension>> theDimensions = new ArrayList<>()
        ArrayList<Integer> columnMax = new ArrayList<>()
        for (int col = 0; col < theTable.getColumnCount(); col++) {
            columnMax.add(0)
        }
        for (int row = 0; row < theTable.getRowCount(); row++) {
            //log.debug("looking at row ${row}")
            ArrayList<Dimension> rowArray = new ArrayList<>()
            int rowMax = 0
            for (int col; col < theTable.getColumnCount(); col++) {
               // log.debug("for column ${col}")
                TableCellRenderer renderer = theTable.getCellRenderer(row, col)
                Component comp = theTable.prepareRenderer(renderer, row, col)
                comp.setSize(theTable.getColumnModel().getColumn(col).getWidth(), Integer.MAX_VALUE)
                int thisHeight = comp.getPreferredSize().height
                int thisWidth = comp.getPreferredSize().width
                columnMax.set(col, Math.max(columnMax.get(col), thisWidth))
                Dimension thisDim = new Dimension(thisWidth, thisHeight)
                rowArray.add(thisDim)
                rowMax = Math.max(rowMax, thisDim.height)
            }
            theDimensions.add(rowArray)
            theTable.setRowHeight(row, rowMax + 3)
          //  log.debug("Row height for row ${row} set to ${rowMax+3}")
        }

        for (int col = 0; col < columnMax.size(); col++) {
            //log.debug("col ${col} set to size ${columnMax.get(col)}")
            TableColumn thisColumn = theTable.getColumnModel().getColumn(col)
            thisColumn.setPreferredWidth(columnMax.get(col)+ 5)
            thisColumn.setMinWidth(columnMax.get(col) + 3)
        }
        return theDimensions
    }

    public void init() {
        thisDialog = new OpDialog(parent, "Car Movement Data", true)
        thisDialog.setName("table")
        thisDialog.setLayout(new BorderLayout())
        JMenuBar menuBar = new JMenuBar()
        ButtonGroup sortGroup = new ButtonGroup()
        byReporting = new JRadioButtonMenuItem("By car number")
        byType = new JRadioButtonMenuItem("By car type")
        JMenu sortMenu = new JMenu("Sort by")
        sortGroup.add(byReporting)
        sortGroup.add(byType)
        byType.setSelected(true)
        sortMenu.add(byReporting)
        sortMenu.add(byType)
        if (model.modelParameter.runType == ViewElement.RunType.CAR) {
            menuBar.add(sortMenu)
        }
        JMenuItem fileClose = new JMenuItem("Close", KeyEvent.VK_C)
        menuBar.add(fileClose)
        fileClose.addActionListener(controller.closeAction)
        thisDialog.getContentPane().add(menuBar, BorderLayout.NORTH)
        theTable = new JTable(model.dataForTable, model.columnHeader)
        theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)
        theTable.setFillsViewportHeight(true)
        ViewCellRenderer renderer = new ViewCellRenderer()
        theTable.setDefaultRenderer(Object.class, renderer)

        theTable.setIntercellSpacing(new Dimension(6, 3))
        TableRowSorter sorter = new TableRowSorter<TableModel>(theTable.getModel())
        if (model.modelParameter.runType == ViewElement.RunType.CAR) {
            sorter.setSortable(0, true)
        } else {
            sorter.setSortable(0, false)
        }
        for (i in 1..<model.columnHeader.size()) {
            sorter.setSortable(i, false)
        }
        sorter.setComparator(0, { Object left, Object right ->
            log.debug("Comparing ${left} and ${right}")
            if (!(left instanceof RowElement) | !(right instanceof RowElement)) {
                log.error("trying to compare an incorrect type")
                throw new RuntimeException("Comparing incorrect types in row sorter")
                }
            if (byType.isSelected()) {
                if (left.carType < right.carType) {
                    return -1
                }
                if (left.carType == right.carType) {
                    return 0
                }
                return 1
            } else {
                if (left.carId < right.carType) {
                    return -1
                }
                if (left.carId == right.carType) {
                    return 0
                }
                return 1
            }
            return 0

        })
        theTable.setRowSorter(sorter)
        JScrollPane tableScroll = new JScrollPane(theTable)
        thisDialog.add(tableScroll, BorderLayout.CENTER)
        Toolkit toolkit = Toolkit.getDefaultToolkit()
        int screenWidth = toolkit.getScreenSize().getWidth()
        int screenHeight = toolkit.getScreenSize().getHeight()
        Integer locx = saver.getInt("table", thisDialog.getXname())
        Integer locy = saver.getInt("table", thisDialog.getYname())

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
        if (locx == null) {
            locx = (screenWidth - dialogWidth)/2
            locy = (screenHeight - dialogHeight) / 2
            saver.saveInt("table", thisDialog.getXname(), locx)
            saver.saveInt("table", thisDialog.getYname(), locy)
        }
        thisDialog.setPreferredSize(new Dimension(dialogWidth, dialogHeight))
        thisDialog.setLocation(locx, locy)
        thisDialog.pack()
        calculateCellMax()
        for (int row = 0; row < theTable.getModel().getRowCount(); row++) {
            log.debug("row ${row} has height ${theTable.getRowHeight(row)}")
        }

        thisDialog.setVisible(true)
    }
}
