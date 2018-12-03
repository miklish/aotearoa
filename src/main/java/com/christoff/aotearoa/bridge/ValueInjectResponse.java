package com.christoff.aotearoa.bridge;

public class ValueInjectResponse
{
    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";

    public ValueInjectResponse(String resultMessage, String resultCode) {
        this.resultMessage = resultMessage;
        this.resultCode = resultCode;
    }

    public String resultMessage;
    public String resultCode;
}
