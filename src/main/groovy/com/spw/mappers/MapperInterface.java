package com.spw.mappers;
import com.spw.rr.Car;
import com.spw.rr.Location;
import com.spw.rr.Track;

import java.util.List;
public interface MapperInterface {
     public RunId selectRunId(String runId);

     public void updateRunId(RunId runValue);

     public int insertRunId(RunId runValue);

     public Integer getSequenceMax(String runId);

     public void mergeCar(Car currentCar);

     public void insertRunLoc(RunLoc runLoc);

     public Track getTrack(Track track);

     public void insertTrack(Track track);

     public void updateTrack(Track track);

     public Location getLocation(Location location);

     public void insertLocation(Location location);
}
