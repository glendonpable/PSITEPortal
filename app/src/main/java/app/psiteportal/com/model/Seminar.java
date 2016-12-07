package app.psiteportal.com.model;

import android.graphics.Bitmap;

import app.psiteportal.com.utils.Config;

/**
 * Created by Lawrence on 2/12/2016.
 */
public class Seminar {

    private String id;
    private String seminarName;
    private String date;
    private String startTime;
    private String endTime;
    private String bannerUrl;
    private Bitmap banner;
    private String is_convention;

    private Seminar(){
        //empty constructor
    }

    public Seminar(String id, String seminarName, String date, String endTime, String startTime, String bannerUrl) {
        this.id = id;
        this.seminarName = seminarName;
        this.date = date;
        this.endTime = endTime;
        this.startTime = startTime;
        this.bannerUrl = bannerUrl;
    }

    public Seminar(String id, String seminarName, String bannerUrl, String is_convention) {
        this.is_convention = is_convention;
        this.id = id;
        this.seminarName = seminarName;
        this.bannerUrl = Config.ROOT_URL + Config.SEMINAR_BANNER + bannerUrl;
    }

    public Seminar(String id, String seminarName, String date){
        this.id = id;
        this.seminarName = seminarName;
        this.date = date
;    }

    public String getIs_convention() {
        return is_convention;
    }

    public void setIs_convention(String is_convention) {
        this.is_convention = is_convention;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeminarName() {
        return seminarName;
    }

    public void setSeminarName(String seminarName) {
        this.seminarName = seminarName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBannerUrl() {
        return bannerUrl;
    }

    public void setBannerUrl(String bannerUrl) {
        this.bannerUrl = bannerUrl;
    }

    public Bitmap getBanner() {
        return banner;
    }

    public void setBanner(Bitmap banner) {
        this.banner = banner;
    }
}
