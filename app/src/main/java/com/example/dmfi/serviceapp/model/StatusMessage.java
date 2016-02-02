package com.example.dmfi.serviceapp.model;

import java.io.Serializable;

/**
 * Created by dmfi on 11/10/2015.
 */
public class StatusMessage implements Serializable {

    private static final long serialVersionUID = 5625767022510099103L;

    private String deviceId;
    private int statusCode;

    public StatusMessage(String deviceId, int statusCode) {
        this.deviceId = deviceId;
        this.statusCode = statusCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
