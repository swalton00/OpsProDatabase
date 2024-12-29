package com.spw.view;

import java.util.List;

public interface ViewInterface {
    public List<ViewCar> listViewCars(String runId) ;
    public List<ViewTrack> listViewLocs(String runId);
}
