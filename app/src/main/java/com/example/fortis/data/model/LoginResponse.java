package com.example.fortis.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    // Getters and Setters are required for Gson to work
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}