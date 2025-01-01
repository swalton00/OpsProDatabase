package com.spw.view

import com.spw.utility.OpDialog
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.awt.event.ActionEvent

class ViewTableController {
    String runId
    ViewParameter viewParameter
    private static final Logger log = LoggerFactory.getLogger(ViewTableController.class)
    ViewTableModel model
    ViewTableView view

    /**
     * create a new controller to the Table display of results
     * @param runId         the selector for the data
     * @param parentDialog  reference to parent dialog
     * @param parameters    all the parameters need to customize the view
     */
    public ViewTableController(String runId, OpDialog parentDialog, ViewParameter parameters) {
        this.runId = runId
        this.viewParameter = parameters
        model = new ViewTableModel()
        view = new ViewTableView()
        view.model = model
        view.controller = this
        view.parent = parentDialog
    }

    void init() {
        log.debug("initializing the ViewTableController class")
        model.init()
        view.init()
    }

    def closeAction = { ActionEvent e ->
        log.debug("cloeing the table view")
        view.thisDialog.setVisible(false)
    }

}
