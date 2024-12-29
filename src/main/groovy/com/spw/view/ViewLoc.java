package com.spw.view;

import java.util.ArrayList;
import java.util.List;

public class ViewLoc {

    Integer id;
    String xmlId;
    String name;
    List<ViewTrack> tracks = new ArrayList<>();
    public List<ViewTrack> getTracks() {
        return tracks;
    }


    public void setTracks(List<ViewTrack> tracks) {
        this.tracks = tracks;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getXmlId() {
        return xmlId;
    }

    public void setXmlId(String xmlId) {
        this.xmlId = xmlId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
