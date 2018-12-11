package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.intern.gateway.metadata.*;

import java.util.*;

/***
certificates:
  tableau-webserver-cert:                                   # this is an internal reference name for the keystore
    format: pem                                             # certificate format
    prompt-text: Tableau webserver public certificate       # prompt text
    file-name: tableauUAT.pem                               # filename of keystore

keystores:
  - output-keystore-filename: {{server.keystore-filename}}: # this will be the output keystore filename
    base-keystore-action: create-new                        # [ create-new , use-existing ]
    base-keystore-filename:                                 # base keystore filename (leave blank if none used)
    keystore-password: password                             # if 'create-new' used this is the new password
                                                            # if 'use-existing' then this is the existing keystore's password
    certificates:                                           # list of certificates to load into keystore
      - tableau-webserver-cert: tableau-uat                 # certificate-reference : alias
*/
public class KeystoreMetadataFileGateway implements IKeystoreMetadataGateway
{
    private static String CERTIFICATES = "certificates";
    private static String KEYSTORES = "keystores";
    
    private String _keystoreMetadataFilename;
    private String _outputDir;
    
    private PersistenceFileHelper _fileSysHelper = new PersistenceFileHelper();
    
    private List<Map<String,Object>> _keystoreMetadata = null;
    private Map<String,Object> _certificateMetadata = null;
    
    public KeystoreMetadataFileGateway(String keystoreMetadataFilename, String outputDir) {
        _keystoreMetadataFilename = keystoreMetadataFilename;
        _outputDir = outputDir;
    }
    
    private void loadMaps() throws MetadataIOException
    {
        if(_keystoreMetadataFilename == null || _keystoreMetadataFilename.equals(""))
            return;
    
        Map<String, Object> allKeystoreConfigMap =
            _fileSysHelper.getFileInfo(_keystoreMetadataFilename, true, false).map;
    

        // Keystore/Cert metadata is optional, so always return a non-null Map
        
        // - get certificate metadata
        Map<String,Object> testCerts = (Map<String, Object>) allKeystoreConfigMap.get(CERTIFICATES);
        _certificateMetadata = testCerts != null ? testCerts : new HashMap<>();
        
        // - get keystore metadata
        List<Map<String,Object>> testKeystores = (List<Map<String,Object>>) allKeystoreConfigMap.get(KEYSTORES);
        _keystoreMetadata = (testKeystores != null && testKeystores.size() > 0) ? testKeystores : new ArrayList<>();
    }
    
    public List<Map<String, Object>> getKeystoreMap()
        throws MetadataException
    {
        if(_keystoreMetadata == null) loadMaps();
        return _keystoreMetadata;
    }
    
    public Map<String,Object> getCertificateMap()
        throws MetadataException
    {
        if(_certificateMetadata == null) loadMaps();
        return _certificateMetadata;
    }
}
