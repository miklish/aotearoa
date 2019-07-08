package com.christoff.aotearoa.intern.gateway.transform;

public interface ITransformGateway
{
    public static String COPY = "copy";
    public static String ENCRYPT = "encrypt";
    public static String COMMA_SEPARATED = "comma-separated";
    public static String DECRYPT = "decrypt";
    
    ITransform get(String transformKey);
}
