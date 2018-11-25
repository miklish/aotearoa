package com.christoff.aotearoa.bridge;

public class ServiceRequest
{
    // This is a link to the _diff data. It could be a file location or url or other
    // depending on how IServiceConfigDataGateway is implemented
    public String diffFileId;
    
    // This is thr root location of the config files. It could a directory of base url or other
    // depending on how IServiceConfigDataGateway is implemented
    public String configRoot;
}
