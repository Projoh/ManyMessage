package com.projoh.android.manymessage;

import android.widget.ImageView;

import java.io.Serializable;

/**
 * Created by Mohamed on 5/23/2016.
 */
public class Person implements Serializable {
    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    String name;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    String fname;
    String lname;
    String phone;
    ImageView image;
    boolean firstletter = false;
    boolean selected;

    public String getThefirstletter() {
        return thefirstletter;
    }

    public void setThefirstletter(String thefirstletter) {
        this.thefirstletter = thefirstletter;
    }

    String thefirstletter;

    public boolean isFirstletter() {
        return firstletter;
    }

    public void setFirstletter(boolean firstletter) {
        this.firstletter = firstletter;
    }



    public Person(String name, String phone) {
        this.name = name;
        this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
        if(name.split("\\w+").length>1){
            lname = name.substring(name.lastIndexOf(" ")+1);
            fname = name.substring(0, name.lastIndexOf(' '));
        }
        else{
            fname = name;
        }

        phone = phone.replaceAll("\\W+", "");
        char firstchar = phone.charAt(0);
        if(Character.getNumericValue(firstchar)== 1) {
            phone = "+"+phone;
        }else {
            phone = "+1"+phone;
        }

        this.phone = phone;
    }

    public Person(String name, String phone, ImageView image) {
        this.name = name;
        this.name = name.substring(0, 1).toUpperCase() + name.substring(1);
        if(name.split("\\w+").length>1){
            lname = name.substring(name.lastIndexOf(" ")+1);
            fname = name.substring(0, name.lastIndexOf(' '));
        }
        else{
            fname = name;
        }
        phone = phone.replaceAll("\\W+", "");
        if(phone.charAt(0) == 1) {
            phone = "+"+phone;
        }else {
            phone = "+1"+phone;
        }
        this.phone = phone;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Person person = (Person) o;
        return name != null ? name.equals(person.name) : person.name == null && (phone != null ? phone.equals(person.phone) : person.phone == null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        return result;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public boolean isSelected() {
        return selected;
    }
    public ImageView getImage() {
        return image;
    }

    public String toString() {
        String linesp = System.getProperty("line.separator");
        return name + " " + phone + linesp;
    }
}
