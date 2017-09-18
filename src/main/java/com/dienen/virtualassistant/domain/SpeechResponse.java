package com.dienen.virtualassistant.domain;

import java.io.Serializable;

/**
 * Created by preetraj on 9/15/17.
 */
public class SpeechResponse implements Serializable {

    private String response;

    public SpeechResponse() {
    }

    public SpeechResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
