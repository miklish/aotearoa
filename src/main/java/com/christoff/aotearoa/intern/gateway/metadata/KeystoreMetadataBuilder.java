package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KeystoreMetadataBuilder
{
    private Map<String,CertificateMetadata> _certMap;
    private Map<String,KeystoreMetadata> _keystoreMap;
    
    public KeystoreMetadataBuilder(Map<String,Object> certObjMap, Map<String,Object> keyObjMap)
    {
        _certMap = buildCertificates(certObjMap);
        _keystoreMap = buildKeystores(keyObjMap, _certMap);
    }
    
    public Map<String,CertificateMetadata> getCertificates()
    {
        return _certMap;
    }
    
    public Map<String,KeystoreMetadata> getKeystores()
    {
        return _keystoreMap;
    }

    private Map<String,CertificateMetadata> buildCertificates(Map<String,Object> certObjMap)
    {
        Map<String,CertificateMetadata> certMap = new HashMap<>();
        for(Map.Entry<String,Object> certEntry : certObjMap.entrySet())
        {
            Map<String,Object> propertiesMap = null;
            try {
                propertiesMap = (Map<String, Object>) certEntry.getValue();
            } catch (ClassCastException e) {
                throw new MetadataException("Certificate metadata is incorrectly formatted");
            }
            
            String name = certEntry.getKey();
            CertificateMetadata cm = new CertificateMetadata();
            cm.setName(name);
            cm.setFilename((String) propertiesMap.get("filename"));
            cm.setFormat((String) propertiesMap.get("format"));
            cm.setPromptText((String) propertiesMap.get("prompt-text"));
            
            certMap.put(name, cm);
        }
        return certMap;
    }
    
    public Map<String,KeystoreMetadata> buildKeystores(Map<String,Object> keyObjMap, Map<String,CertificateMetadata> certMap)
    {
        Map<String,KeystoreMetadata> keyMap = new HashMap<>();
        for(Map.Entry<String,Object> keyEntry : keyObjMap.entrySet())
        {
            Map<String,Object> propertiesMap = null;
            try {
                propertiesMap = (Map<String, Object>) keyEntry.getValue();
            } catch (ClassCastException e) {
                throw new MetadataException("Certificate metadata is incorrectly formatted");
            }
        
            String outputKeystoreFilename = keyEntry.getKey();
            KeystoreMetadata km = new KeystoreMetadata();
            km.setOutputKeystoreFilename(outputKeystoreFilename);
            km.setBaseKeystoreAction((String) propertiesMap.get("base-keystore-action"));
            km.setBaseKeystoreFilename((String) propertiesMap.get("base-keystore-filename"));
            km.setKeystorePassword((String) propertiesMap.get("keystore-password"));
            
            Map<String,Object> certsObjectMap = null;
            try {
                certsObjectMap = (Map<String,Object>) propertiesMap.get("certificates");
            } catch(ClassCastException e) {
                throw new MetadataException("Certificate metadata is incorrectly formatted");
            }
            
            List<CertificateMetadata> certList = new LinkedList<>();
            for(Map.Entry<String,Object> certEntry : certsObjectMap.entrySet())
            {
                String certRef = certEntry.getKey();
                String certAlias = (String) certEntry.getValue();
                CertificateMetadata cert = certMap.get(certRef);
                km.addCertByAlias(certAlias,cert);
                certList.add(cert);
            }
            km.setCertificates(certList);
            keyMap.put(outputKeystoreFilename, km);
        }
        return keyMap;
    }
}
