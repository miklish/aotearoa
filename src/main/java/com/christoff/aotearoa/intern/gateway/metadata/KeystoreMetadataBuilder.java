package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeystoreMetadataBuilder
{
    public static Map<String, CertificateMetadata> buildCertificateMetadata(Map<String,Object> certObjMap, List<Map<String,Object>> keystoreObjMap)
    {
        Map<String, CertificateMetadata> certMap = new HashMap<>();
        for(Map.Entry<String,Object> certEntry : certObjMap.entrySet())
        {
            String name = certEntry.getKey();
            Map<String,Object> propertiesMap = (Map<String,Object>) certEntry.getValue();
            
            String filename = (String) propertiesMap.get("filename");
            String promptText = (String) propertiesMap.get("prompt-text");
            String format = (String) propertiesMap.get("format");
            
            CertificateMetadata cm = new CertificateMetadata();
            cm.setFilename(filename);
            cm.setName(name);
            cm.setFormat(format);
            cm.setPromptText(promptText);
            
            certMap.put(name, cm);
            
            System.out.println(cm.toString());
        }
        
        return certMap;
    }
}
