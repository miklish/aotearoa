package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/***
- output-keystore-filename: {{server.keystore-filename}}: # this will be the output keystore filename
  base-keystore-action: create-new                        # [ create-new , use-existing ]
  base-keystore-filename:                                 # base keystore filename (leave blank if none used)
  keystore-password: password                             # if 'create-new' used this is the new password
                                                          # if 'use-existing' then this is the existing keystore's password
  certificates:                                           # list of certificates to load into keystore
    - tableau-webserver-cert: tableau-uat                 # certificate-reference : alias
 
 */

public class KeystoreMetadata
{
    private String _outputKeystoreFilename;
    private String _baseKeystoreAction;
    private String _baseKeystoreFilename;
    private String _keystorePassword;
    private List<CertificateMetadata> _certificates = new LinkedList<>();
    private Map<String,CertificateMetadata> _certByAlias = new HashMap<>();
    
    public String getOutputKeystoreFilename() {
        return _outputKeystoreFilename;
    }
    
    public String getBaseKeystoreAction() {
        return _baseKeystoreAction;
    }
    
    public String getBaseKeystoreFilename() {
        return _baseKeystoreFilename;
    }
    
    public String getKeystorePassword() {
        return _keystorePassword;
    }
    
    public List<CertificateMetadata> getCertificates() {
        return _certificates;
    }
    
    public void setOutputKeystoreFilename(String outputKeystoreFilename) {
        _outputKeystoreFilename = outputKeystoreFilename;
    }
    
    public void setBaseKeystoreAction(String baseKeystoreAction) {
        _baseKeystoreAction = baseKeystoreAction;
    }
    
    public void setBaseKeystoreFilename(String baseKeystoreFilename) {
        _baseKeystoreFilename = baseKeystoreFilename;
    }
    
    public void setKeystorePassword(String keystorePassword) {
        _keystorePassword = keystorePassword;
    }
    
    public void setCertificates(List<CertificateMetadata> certificates) {
        _certificates = certificates;
    }
    
    public CertificateMetadata getCertByAlias(String alias) {
        return _certByAlias.get(alias);
    }
    
    public void addCertByAlias(String alias, CertificateMetadata cert) {
        _certByAlias.put(alias,cert);
    }
    
    @Override
    public String toString() {
        return "KeystoreMetadata{" +
            "outputKeystoreFilename='" + _outputKeystoreFilename + '\'' +
            ", baseKeystoreAction='" + _baseKeystoreAction + '\'' +
            ", baseKeystoreFilename='" + _baseKeystoreFilename + '\'' +
            ", keystorePassword='" + _keystorePassword + '\'' +
            ", certificates=" + _certificates.toString() +
            '}';
    }
}
