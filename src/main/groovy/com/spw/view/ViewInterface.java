package com.spw.view;

import org.h2.result.Row;

import java.util.List;

public interface ViewInterface {
    public List<ViewCar> listViewCars(String runId) ;
    public List<ViewTrack> listViewLocs(String runId);
    public List<Integer> getSequenceNumbers(String runId);
    public List<RowElement> listRows(String runId);
}
