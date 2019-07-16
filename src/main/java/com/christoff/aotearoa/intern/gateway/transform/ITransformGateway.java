package com.christoff.aotearoa.intern.gateway.transform;

public interface ITransformGateway
{
    String COPY = "copy";
    String ENCRYPT = "encrypt";
    String COMMA_SEPARATED = "comma-separated";
    String DECRYPT = "decrypt";
    
    ITransform get(String transformKey);
}
