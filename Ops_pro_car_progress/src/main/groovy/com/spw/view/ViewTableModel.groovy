package com.spw.view

import com.spw.utility.OpDialog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ViewTableModel {

    Logger log = LoggerFactory.getLogger(ViewTableModel.class)
    Vector<String> columnHeader
    ViewParameter modelParameter
    Vector<Vector<Object>> dataForTable

    public void init() {

    }
}
