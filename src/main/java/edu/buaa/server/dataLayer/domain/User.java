package edu.buaa.server.dataLayer.domain;

import edu.buaa.server.util.JSON;

import java.io.Serializable;
import java.math.BigInteger;


enum UserState {
    ENABLE,
    DISABLE
}

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    public BigInteger id;
    public String nickname;
    public String phone;
    public String email;
    public BigInteger balance;
    public String password;
    public String avator;
    public UserState state;

    public User() {
    }

    public User(String nickname, String phone, String email, String password) {
        this.nickname = nickname;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    public JSON toJsonInManagerSearch() {
        JSON user = new JSON();
        user.put("id", id);
        user.put("user_name", nickname);
        user.put("cellphone", phone);
        user.put("email", email);
        user.put("avator", avator);
        if (state == UserState.DISABLE)
            user.put("state", -1);
        else
            user.put("state", 0);
        return user;
    }
}
