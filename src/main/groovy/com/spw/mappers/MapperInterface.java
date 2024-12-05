package com.spw.mappers;
import com.spw.rr.Car;
import com.spw.rr.Location;

import java.util.List;
public interface MapperInterface {
     public List<RunId> selectRunId(RunId runValue);

     public void updateRunId(RunId runValue);

     public int insertRunId(RunId runValue);

     public int insertSequence(SequenceValue newSeq);

     public void updateSequence(SequenceValue newSeq);

     public List<SequenceValue> getSequenceList(String runid);

     public void mergeCar(Car currentCar);

     public void insertRunLoc(RunLoc runLoc);

     public void mergeLocation(Location location);

}
