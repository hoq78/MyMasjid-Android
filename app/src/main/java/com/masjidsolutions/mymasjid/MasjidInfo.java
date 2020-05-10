package com.masjidsolutions.mymasjid;

import java.io.Serializable;


public class MasjidInfo implements Serializable {

    private String name;
    private String phonenumber;
    private String postcode;
    private String csvurl;
    private String messageurl;
    private String roadno;
    private String roadname;
    private String videourl;
    private String audiourl;

    public MasjidInfo(String name, String phonenumber, String postcode, String csvurl, String messageurl, String roadno, String roadname, String videourl, String audiourl) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.postcode = postcode;
        this.csvurl = csvurl;
        this.messageurl = messageurl;
        this.roadno = roadno;
        this.roadname = roadname;
        this.videourl = videourl;
        this.audiourl = audiourl;
    }

    public String getName() {
        return name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCsvurl() {
        return csvurl;
    }

    public String getMessageurl() {
        return messageurl;
    }

    public String getRoadno() {
        return roadno;
    }

    public String getRoadname() {
        return roadname;
    }

    public String getVideourl() {
        return videourl;
    }

    public String getAudiourl() {
        return audiourl;
    }
}