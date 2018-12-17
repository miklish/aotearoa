package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class KeystoreMetadataBuilder
{
    /***
     * Builds Java objects containing parsed metadata that describes the Keystores that the user
     * wishes to build.
     */
    
    private Map<String,CertificateMetadata> _certMap;
    private Map<String,KeystoreMetadata> _keystoreMap;
    
    /***
     *
     * @param certObjMap Certificate raw metadata map (from IKeystoreMetadataGateway)
     * @param keyObjMap Keystore raw metadata map (from IKeystoreMetadataGateway)
     */
    public KeystoreMetadataBuilder(Map<String,Object> certObjMap, Map<String,Object> keyObjMap)
    {
        _certMap = buildCertificates(certObjMap);
        _keystoreMap = buildKeystores(keyObjMap, _certMap);
    }
    
    /***
     * Returns a map of certificates, keyed by certificate-reference
     *
     * @return A map of certificates, keyed by certificate-reference
     *         Never null (if no certificates exist, returns an empty map)
     */
    public Map<String,CertificateMetadata> getCertificates()
    {
        return _certMap;
    }
    
    /***
     * Returns a map of Keystores, keyed by keystore file name
     *
     * @return A map of keystores, keyed by keystore file name
     *         Never null (if no keystores exist, returns an empty map)
     */
    public Map<String,KeystoreMetadata> getKeystores()
    {
        return _keystoreMap;
    }
    
    /***
     * Builds a map of Certificates, keyed by certificate-reference
     *
     * @param certObjMap Certificate raw metadata map (from IKeystoreMetadataGateway)
     * @return Certificate metadata map (Map: certificate-reference -> Certificate)
     *         Never returns null: If no certificates exist, it will return an empty map
     */
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
    
    /***
     * Builds a map of Keystores, keyed by the keystore filename
     *
     * @param keyObjMap Keystore raw metadata map (from IKeystoreMetadataGateway)
     * @param certMap Certificate metadata map (Map: certificate reference -> Certificate)
     * @return Keystore metadata map (Map: keystore file name -> Keystore)
     *         Never returns null: If no keystores exist, it will return an empty map
     */
    private Map<String,KeystoreMetadata> buildKeystores(Map<String,Object> keyObjMap, Map<String,CertificateMetadata> certMap)
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
