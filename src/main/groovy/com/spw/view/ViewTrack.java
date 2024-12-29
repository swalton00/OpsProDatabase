package com.spw.view;

public class ViewTrack {
    Integer id;
    Integer parentId;
    String  parentXMLid;

    String trackName;

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    String trkId;
    String tracktype;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getParentXMLid() {
        return parentXMLid;
    }

    public void setParentXMLid(String parentXMLid) {
        this.parentXMLid = parentXMLid;
    }

    public String getTrkId() {
        return trkId;
    }

    public void setTrkId(String trkId) {
        this.trkId = trkId;
    }

    public String getTracktype() {
        return tracktype;
    }

    public void setTracktype(String tracktype) {
        this.tracktype = tracktype;
    }

    public String toString() {
        return trackName;
    }
}
