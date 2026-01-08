package com.spw.view

import com.spw.mappers.RunId
import com.spw.utility.OpDialog
import com.spw.utility.PropertySaver
import net.miginfocom.swing.MigLayout
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.swing.JDialog
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.TableCellRenderer
import javax.swing.table.TableColumnModel
import java.awt.BorderLayout
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
    static final Integer HEIGHT_MULTIPLIER = 22
    static final Integer WIDTH_MULTIPLIER = 8

    /**
     * Calculate the minimum roow heights and widths based on the run type and table data
     * @param widths
     * @param height
     * @param isCars
     * @param tabData
     * @param table
     *       For cars - every row has at least height 2 and should be constant
     *          column width is mox of roadname or roadnumber,
     *          others - location or trackname
     *          height is a minimum of 2, depending on number in inner cars
     *          width is largest of (column 1 - location or trackName)
     *          other columns - size of carid + size of load + 1
     */
    private void cellMaxes(ArrayList<Integer> widths,
                           ArrayList<Integer> heights,
                           boolean isCars,
                           Vector<Vector<Object>> tabData,
                           JTable table) {
        tabData.eachWithIndex{ Vector<Vector<Object>> row, int i ->
            row.eachWithIndex{ Object entry, int j ->
                table.getCellRenderer(i, j).getCellMaxes(widths, heights, entry, j, i)
            }
        }
    }

    private void tableSizing(JTable tab) {
        ArrayList<Integer> rowHeights = new ArrayList<>()
        ArrayList<Integer> rowWidths = new ArrayList<>()
        for (i in 0..<model.columnHeader.size()) {
            rowWidths.add(Integer.valueOf(0))
        }
        for (i in 0..<model.dataForTable.size()) {
            rowHeights.add(Integer.valueOf(0))
        }
        boolean isCar
        if (model.modelParameter.runType.equals(ViewElement.RunType.CAR)){
            isCar = true
        } else if (model.modelParameter.runType.equals(ViewElement.RunType.TRACK)) {
            isCar = false
        } else {
            log.error("modelParameter has unrecognized runType - ${model.modelParameter.runType}")
        }
        cellMaxes(rowWidths, rowHeights, isCar, model.dataForTable, tab)
        TableColumnModel columnModel = tab.getColumnModel()
   //     log.trace("column width multiplier is ${WIDTH_MULTIPLIER}")
        for (i in 0..<model.columnHeader.size()) {
       //     log.trace("column width for column ${i} is ${rowWidths.getAt(i)}")
            columnModel.getColumn(i).setPreferredWidth((rowWidths.get(i)*WIDTH_MULTIPLIER))
        }
     //   log.trace("row height multiplier is ${HEIGHT_MULTIPLIER}")
        for (i in 0..<model.dataForTable.size()) {
     //       log.trace("row height for row ${i} is ${rowHeights.get(i)}")
            tab.setRowHeight(i, (rowHeights.get(i) * HEIGHT_MULTIPLIER))
        }
        // put cell maxes here

    }


    public void init() {
        thisDialog = new OpDialog(parent, "Car Movement Data", true)
        thisDialog.setName("table")
        thisDialog.setLayout(new BorderLayout())
        JMenuBar menuBar = new JMenuBar()
        JMenu fileMenu = new JMenu("File")
        menuBar.add(fileMenu, "wrap")
        JMenuItem fileClose = new JMenuItem("Close", KeyEvent.VK_C)
        menuBar.add(fileClose)
        fileClose.addActionListener(controller.closeAction)
        thisDialog.getContentPane().add(menuBar, BorderLayout.NORTH)
        theTable = new JTable(model.dataForTable, model.columnHeader)
        theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF)
        theTable.setFillsViewportHeight(true)
        ViewCellRenderer renderer = new ViewCellRenderer()
        theTable.setDefaultRenderer(Object.class, renderer)
        tableSizing(theTable)
        theTable.setIntercellSpacing(new Dimension(6, 3))
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
        thisDialog.setVisible(true)
    }
}
