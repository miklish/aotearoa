package com.christoff.aotearoa.bridge;

public class ServiceResponse
{
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public ServiceResponse(String resultMessage, String resultCode) {
        this.resultMessage = resultMessage;
        this.resultCode = resultCode;
    }

    public String resultMessage;
    public String resultCode;
}
