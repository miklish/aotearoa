package com.christoff.aotearoa.bridge;

/***
 * TODO: Update to return fully injected output as Map/List structure
 */
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
