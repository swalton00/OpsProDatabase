package com.spw.view

import javax.swing.JList
import javax.swing.JTable
import javax.swing.table.TableCellRenderer
import java.awt.Component

class ViewCellRenderer extends JList<String[]> implements TableCellRenderer {
    @Override
    Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String[] resultValue
        if (value instanceof RowElement) {
            resultValue = new String[2]
            if (ViewElement.thisRun.equals(ViewElement.RunType.CAR)) {
                resultValue[0] = value.roadName
                resultValue[1] = value.roadNumber
            } else {
                resultValue[0] = value.location
                resultValue[1] = value.trackName
            }
        } else if (value instanceof ViewElement) {
            if (ViewElement.thisRun.equals(ViewElement.RunType.CAR)) {
                resultValue = new String[2]
                resultValue[0] = value.location
                resultValue[1] = value.trackName + "-" + value.load
            } else {
                resultValue = new String[value.carList.size()]
                int whichElement = 0
                value.carList.each {
                    resultValue[whichElement++] = it.carId + "-" + it.load
                }
            }
        } else {
            resultValue = new String[1]
            resultValue[0] = " "
        }
        setListData((String[]) resultValue)
        return this
    }
}
