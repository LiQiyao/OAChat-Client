package com.yytech.ochatclient.dto.data;

import java.io.Serializable;

public class UserInfo implements Serializable{
    private Long id;

    private String username;

    private String password;

    private String nickName;

    private String gender;

    private String address;

    private String telephoneNumber;

    private String icon;

    public UserInfo(Long id, String username, String password, String nickName, String gender, String address, String telephoneNumber, String icon) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickName = nickName;
        this.gender = gender;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.icon = icon;
    }

    public UserInfo() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender == null ? null : gender.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber == null ? null : telephoneNumber.trim();
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }
}