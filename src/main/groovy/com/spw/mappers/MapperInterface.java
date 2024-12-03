package com.spw.mappers;
import java.util.List;
public interface MapperInterface {
     public List<RunId> selectRunId(RunId runValue);

     public void updateRunId(RunId runValue);

     public int insertRunId(RunId runValue);

     public int insertSequence(SequenceValue newSeq);

     public void updateSequence(SequenceValue newSeq);

     public int getSequenceCount(String runid);

}
