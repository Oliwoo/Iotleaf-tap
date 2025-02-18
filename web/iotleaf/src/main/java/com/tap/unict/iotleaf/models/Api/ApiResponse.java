package com.tap.unict.iotleaf.models.Api;

import java.io.Serializable;

public class ApiResponse implements Serializable {
    private Object data;
    private boolean success;
    private String error;
    private Long timestamp;

    public ApiResponse(String error, Object o){
        this.success = error==null;
        this.error = error;
        this.data = o;
        this.timestamp = System.currentTimeMillis();
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    
}
