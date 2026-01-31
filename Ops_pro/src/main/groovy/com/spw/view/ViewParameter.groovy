package com.spw.view

import groovy.transform.ToString

@ToString(includePackage = false, includeFields = true, includeNames = true)
class ViewParameter {
    ViewElement.RunType runType = ViewElement.RunType.CAR
    String runId
    public enum CarSelect {
        ALL, MOVED, SPECIFIC, CAR_TYPE
    }

    public enum LocSelect {
        ALL, WITH, MOVED, SPECIFIC
    }
    CarSelect carSelect = CarSelect.ALL
    LocSelect locSelect = LocSelect.ALL
    List<String> idList
    boolean specificInclude = true
}
