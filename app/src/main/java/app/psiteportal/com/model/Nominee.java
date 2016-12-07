package app.psiteportal.com.model;

import android.graphics.Bitmap;
import android.widget.CheckBox;

import app.psiteportal.com.utils.Config;

/**

 * Created by fmpdroid on 1/28/2016.
 */
public class Nominee{

    private String imageUrl, name, institution, contact, email, address, count, username;
    private Bitmap bitmap;
    private boolean isSelected;
    public Nominee(){
    }
    public Nominee(String name){
        this.name = name;
    }

    public Nominee(String imageUrl, String name, String institution, String contact, String email, String address, String count, boolean isSelected) {
        this.institution = institution;
        this.imageUrl = Config.ROOT_URL + Config.PROF_PICS + imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.count = count;
        this.isSelected = isSelected;
    }

    public Nominee(String imageUrl,String username, String name, String institution, String contact, String email, String address) {
        this.username = username;
        this.institution = institution;
        this.imageUrl = Config.ROOT_URL + Config.PROF_PICS + imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
    }
    public Nominee(String imageUrl, String username, String name, String institution, String contact, String email, String address, boolean isSelected, String nothing) {
        this.username = username;
        this.institution = institution;
        this.imageUrl = Config.ROOT_URL + Config.PROF_PICS + imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.isSelected = isSelected;
    }
    public Nominee(String imageUrl,String username, String name, String institution, String contact, String email, String address, String count) {
        this.username = username;
        this.institution = institution;
        this.imageUrl = Config.ROOT_URL + Config.PROF_PICS + imageUrl;
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.count = count;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}