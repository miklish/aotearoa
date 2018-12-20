package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.intern.gateway.metadata.*;

import java.io.File;
import java.util.*;

/***

certificates:
 
  tableau-webserver-cert:                                   # this is an internal reference name for the keystore
    format: pem                                             # certificate format
    prompt-text: Tableau webserver public certificate       # prompt text
    filename: tableauUAT.pem                                # filename of keystore

  postgres-database-cert:                                   # this is an internal reference name for the keystore
    format: pem                                             # certificate format
    prompt-text: Postgres self-signed certificate           # prompt text
    filename: server.pem                                    # filename of keystore

keystores:
 
  {{server-truststore-filename}}:                           # this will be the output keystore filename
    base-keystore-action: create-new                        # [ create-new , use-existing ]
    base-keystore-filename: x                               # base keystore filename (leave blank if none used)
    keystore-password: password                             # 'create-new' -> new keystore password,
                                                            # 'use-existing' -> existing keystore password
 
    certificates:                                           # list of certificates to load into keystore
      tableau-webserver-cert: tableau-uat                   # certificate-reference : alias
      postgres-database-cert: postgres-uat
 
*/
public class KeystoreMetadataFileGateway implements IKeystoreMetadataGateway
{
    private static String CERTIFICATES = "certificates";
    private static String KEYSTORES = "keystores";
    
    private String _keystoreMetadataFilename;
    private String _outputDir;
    
    private PersistenceFileHelper _fileSysHelper = new PersistenceFileHelper();
    
    private Map<String,Object> _keystoreMetadata = null;
    private Map<String,Object> _certificateMetadata = null;
    
    public KeystoreMetadataFileGateway(String keystoreMetadataFilename, String outputDir) {
        _keystoreMetadataFilename = keystoreMetadataFilename;
        _outputDir = outputDir;
    }
    
    private void loadMaps()
        throws MetadataException
    {
        // Keystore/Cert metadata is optional, so always return a non-null Map
        _keystoreMetadata = new HashMap<>();
        _certificateMetadata = new HashMap<>();
        
        if(_keystoreMetadataFilename == null) return;
        
        // attempt to load/parse yaml (this will throw exception if yaml not valid)
        File f = new File(_keystoreMetadataFilename);
        if(!f.exists() || !f.isFile()) return;
        
        String resolvedFilename = _outputDir + "/" + f.getName();
        Map<String, Object> allKeystoreConfigMap =
            _fileSysHelper.getFileInfo(resolvedFilename, true, false).map;

        // - get certificate metadata
        Map<String,Object> testCerts = null;
        try {
            testCerts = (Map<String, Object>) allKeystoreConfigMap.get(CERTIFICATES);
        } catch(ClassCastException e) {
            throw new MetadataException("'certificates' section of keystore metadata has an invalid format");
        }
        _certificateMetadata = testCerts != null ? testCerts : _certificateMetadata;
        
        // - get keystore metadata
        Map<String,Object> testKeystores = null;
        try {
            testKeystores = (Map<String, Object>) allKeystoreConfigMap.get(KEYSTORES);
        } catch(ClassCastException e) {
            throw new MetadataException("'keystores' section of keystore metadata has an invalid format");
        }
        _keystoreMetadata = testKeystores != null ? testKeystores : _keystoreMetadata;
    }
    
    public Map<String,Object> getKeystoreMap()
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
