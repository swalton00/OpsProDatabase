package com.spw.rr

import groovy.transform.ToString

@ToString(includeFields = true, includeNames = true, includePackage = false)
@Singleton
class VarData {
    static String dataHome = 'C:/Users/jsw.SPW/JMRI/Ops.jmri/operations'
    static String carFile = 'OperationsCarRoster.xml'
    static String locationsFile = 'OperationsLocationRoster.xml'
    static String dbUrl = 'jdbc:h2:C:/Projects/Ops_pro_Cars/Database/test;AUTO_SERVER=TRUE;SCHEMA=parser'
    static String dbUserid = 'rr'
    static String dbPw = "rrpass"
    static String runId = "A"
    static String runComment = "First test of the program"
}
