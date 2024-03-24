package com.human.wifirepeater.models;
public class WifiModel {
    public String BSSID;
    public String SSID;
    public int	frequency;
    public int	level;
    //public String capabilities;
    public WifiModel(String bssd,String ssid,int freq,int levl){
        this.BSSID=bssd;
        this.SSID=ssid;
        this.frequency= freq;
        this.level = levl;
    }


}
