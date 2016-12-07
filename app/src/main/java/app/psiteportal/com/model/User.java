package app.psiteportal.com.model;

import app.psiteportal.com.utils.Config;

/**
 * Created by Lawrence on 3/16/2016.
 */
public class User {

    private String id;
    private String username;
    private String firstname;
    private String lastname;
    private String gender;
    private String contact;
    private String email;
    private String adress;
    private String prof_pic;
    private String activated;

    private User() {
        //empty
    }

    public User(String id, String username, String firstname, String lastname, String gender, String contact, String email, String adress, String prof_pic, String activated) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.gender = gender;
        this.contact = contact;
        this.email = email;
        this.adress = adress;
        this.prof_pic = Config.ROOT_URL + Config.PROF_PICS + prof_pic;
        this.activated = activated;
    }

    public User(String id, String username, String firstname, String lastname, String prof_pic, String activated, String gender) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.prof_pic = Config.ROOT_URL + Config.PROF_PICS + prof_pic;
        this.activated = activated;
        this.gender = gender;
    }

    public User(String firstname, String lastname, String activated, String prof_pic) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.activated = activated;
        this.prof_pic = Config.ROOT_URL + Config.PROF_PICS + prof_pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getProf_pic() {
        return prof_pic;
    }

    public void setProf_pic(String prof_pic) {
        this.prof_pic = prof_pic;
    }

    public String getActivated() {
        return activated;
    }

    public void setActivated(String activated) {
        this.activated = activated;
    }
}
