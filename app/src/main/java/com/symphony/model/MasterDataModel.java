package com.symphony.model;

/**
 * Created by indianic on 22/05/17.
 */

public class MasterDataModel {
    private String dealerletlongid;
    private String dealerenrolmentid;
    private String lat;
    private String lang;
    private String created_on;
    private String name;
    private String addr;


    public String getDealerletlongid() {
        return dealerletlongid;
    }

    public void setDealerletlongid(String dealerletlongid) {
        this.dealerletlongid = dealerletlongid;
    }

    public String getDealerenrolmentid() {
        return dealerenrolmentid;
    }

    public void setDealerenrolmentid(String dealerenrolmentid) {
        this.dealerenrolmentid = dealerenrolmentid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
