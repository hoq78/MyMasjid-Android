package com.masjidsolutions.mymasjid;

class CsvSample {
    private String Date;
    private String Day;
    private String Fajr;
    private String Sunrise;
    private String Zuhr;
    private String Khutbah;
    private String Asr;
    private String Maghrib;
    private String Isha;
    private String FajrJ;
    private String ZuhrJ;
    private String KhutbahJ;
    private String AsrJ;
    private String MaghribJ;
    private String IshaJ;

    public CsvSample(String date, String day, String fajr, String sunrise, String zuhr, String khutbah, String asr, String maghrib, String isha, String fajrJ, String zuhrJ, String khutbahJ, String asrJ, String maghribJ, String ishaJ) {
        Date = date;
        Day = day;
        Fajr = fajr;
        Sunrise = sunrise;
        Zuhr = zuhr;
        Khutbah = khutbah;
        Asr = asr;
        Maghrib = maghrib;
        Isha = isha;
        FajrJ = fajrJ;
        ZuhrJ = zuhrJ;
        KhutbahJ = khutbahJ;
        AsrJ = asrJ;
        MaghribJ = maghribJ;
        IshaJ = ishaJ;
    }

    public  CsvSample(String error ){
        Date = error;
        Day = error;
        Fajr = error;
        Sunrise = error;
        Zuhr = error;
        Khutbah = error;
        Asr = error;
        Maghrib = error;
        Isha = error;
        FajrJ = error;
        ZuhrJ = error;
        KhutbahJ = error;
        AsrJ = error;
        MaghribJ = error;
        IshaJ = error;
    }

    String getDate() {
        return Date;
    }

     String getFajr() {
        return Fajr;
    }

     String getZuhr() {
        return Zuhr;
    }

     String getAsr() {
        return Asr;
    }

     String getMaghrib() {
        return Maghrib;
    }

     String getIsha() {
        return Isha;
    }

     String getFajrJ() {
        return FajrJ;
    }

     String getZuhrJ() {
        return ZuhrJ;
    }
     String getKhutbahJ() {
        return KhutbahJ;
    }

     String getAsrJ() {
        return AsrJ;
    }


     String getMaghribJ() {
        return MaghribJ;
    }


     String getIshaJ() {
        return IshaJ;
    }

    @Override
    public String toString() {
        return "CsvSample{" +
                "Date='" + Date + '\'' +
                ", Day='" + Day + '\'' +
                ", Fajr='" + Fajr + '\'' +
                ", Sunrise='" + Sunrise + '\'' +
                ", Zuhr='" + Zuhr + '\'' +
                ", Khutbah='" + Khutbah + '\'' +
                ", Asr='" + Asr + '\'' +
                ", Maghrib='" + Maghrib + '\'' +
                ", Isha='" + Isha + '\'' +
                ", FajrJ='" + FajrJ + '\'' +
                ", ZuhrJ='" + ZuhrJ + '\'' +
                ", KhutbahJ='" + KhutbahJ + '\'' +
                ", AsrJ='" + AsrJ + '\'' +
                ", MaghribJ='" + MaghribJ + '\'' +
                ", IshaJ='" + IshaJ + '\'' +
                '}';
    }
}
