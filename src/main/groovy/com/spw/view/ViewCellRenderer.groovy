package com.spw.view

import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.TableCellRenderer
import java.awt.Component

class ViewCellRenderer extends DefaultTableCellRenderer {

    public String convertToHTML(String[] inStrings) {
        //log.debu("converting the string[] ${inStrings} an HTML string")
        StringBuffer sb = new StringBuffer("<html>")
        sb.append(inStrings.join("<br>"))
        sb.append("<html>")
        return sb.toString()
    }

    private String getCellStrings(Object value) {
        String[] resultValue
        if (value instanceof RowElement) {
            resultValue = new String[2]
            if (ViewElement.thisRun.equals(ViewElement.RunType.CAR)) {
                resultValue = new String[3]
                resultValue[0] = value.roadName
                resultValue[1] = "&emsp;" + value.roadNumber
                resultValue[2] = value.carType
            } else {
                resultValue = new String[2]
                resultValue[0] = value.location
                resultValue[1] = value.trackName
            }
        } else if (value instanceof ViewElement) {
            if (ViewElement.thisRun.equals(ViewElement.RunType.CAR)) {
                resultValue = new String[2]
                resultValue[0] = value.location
                resultValue[1] = value.trackName + "&ensp;-&ensp;" + value.load
            } else {
                StringBuffer sb = new StringBuffer("<html><table><tbody>")
                //resultValue = new String[value.carList.size()]
                int whichElement = 0
                value.carList.each {
                    //resultValue[whichElement++] = it.roadName + "&thinsp;" + it.roadNumber+ "&ensp;-&ensp;" + it.load
                    sb.append("<tr><td>")
                    sb.append(it.roadName)
                    sb.append("</td><td>")
                    sb.append(it.roadNumber)
                    sb.append("</td><td>")
                    sb.append(it.load)
                    sb.append("</td>")
                }
                sb.append("</table></html")
                return sb.toString()
            }
        } else {
            resultValue = new String[1]
            resultValue[0] = " "
        }
        return convertToHTML(resultValue)
    }

    @Override
    Component getTableCellRendererComponent(JTable table,
                                            Object value,
                                            boolean isSelected,
                                            boolean hasFocus,
                                            int row,
                                            int column) {
        String resultValue = getCellStrings(value)
        return new JLabel(resultValue)
    }
}
