package app.psiteportal.com.model;

import app.psiteportal.com.utils.Config;

/**
 * Created by fmpdroid on 3/11/2016.
 */
public class Member {

    private String username;
    private String name;
    private String email;
    private String status;

    private String institution;
    private String contact;
    private String address;
    private String imageUrl;

    public Member(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }
    public Member(String username, String name, String email, String status) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.status = status;
    }
    public Member(String imageUrl, String name, String institution, String contact, String email, String address, String status) {
        this.institution = institution;
        this.imageUrl = Config.ROOT_URL + Config.PROF_PICS + imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.status = status;
    }
    public Member(String username, String imageUrl, String name, String institution, String contact, String email, String address, String status) {
        this.username = username;
        this.institution = institution;
        this.imageUrl = Config.ROOT_URL + Config.PROF_PICS + imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.status = status;
    }
    public Member(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
