package com.spw.view;

import org.h2.result.Row;

import java.util.List;

public interface ViewInterface {
    public List<ViewCar> listViewCars(String runId) ;
    public List<ViewTrack> listViewLocs(String runId);
    public List<Integer> getSequenceNumbers(String runId);
    public List<RowElement> listRows(ViewParameter parameter);
    public List<RowElement> listLocRows(ViewParameter parameter);
    public List<ViewType> listViewTypes(String runId);
    public Integer getMoveCount(String runId);
}
