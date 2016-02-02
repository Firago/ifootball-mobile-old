package com.example.dmfi.serviceapp.rest;

import android.os.AsyncTask;
import android.util.Log;

import com.example.dmfi.serviceapp.model.StatusMessage;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by dmfi on 10/10/2015.
 */
public class HttpStatusUpdateTask extends AsyncTask<StatusMessage, Void, Void> {

    private static final String TAG = HttpStatusUpdateTask.class.getSimpleName();

    @Override
    protected Void doInBackground(StatusMessage... params) {
        // get message object from parameters
        final StatusMessage message = params[0];
        Log.d(TAG, String.format("Device=%s, status=%d", message.getDeviceId(), message.getStatusCode()));
        // webservice url
        final String url = "http://wildfly-dfirago.rhcloud.com/status";
        try {
            RestTemplate restTemplate = new RestTemplate();
            // add message converters
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            // make HTTP POST request
            restTemplate.postForObject(url, message.getStatusCode(), Void.class);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }
}
