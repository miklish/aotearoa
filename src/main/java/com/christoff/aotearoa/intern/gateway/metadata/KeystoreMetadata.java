package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/***
- output-keystore-filename: {{server.keystore-filename}}: # this will be the output keystore filename
  base-keystore-filename:                                 # base keystore filename (leave blank if none used)
  keystore-password: password                             # if 'create-new' used this is the new password
                                                          # if 'use-existing' then this is the existing keystore's password
  certificates:                                           # list of certificates to load into keystore
    - tableau-webserver-cert: tableau-uat                 # certificate-reference : alias
 
 */

public class KeystoreMetadata
{
    private String _outputKeystoreFilename;
    private String _baseKeystoreFilename;
    private String _keystorePassword;
    private List<CertificateMetadata> _certificates = new LinkedList<>();
    private Map<String,CertificateMetadata> _certByAlias = new HashMap<>();
    private Map<String,String> _aliasByCertRef = new HashMap<>();
    
    public String getOutputKeystoreFilename() {
        return _outputKeystoreFilename;
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

    public void addAliasByCertRef(String certRef, String alias) { _aliasByCertRef.put(certRef, alias); }

    public String getAliasByCertRef(String certRef) { return _aliasByCertRef.get(certRef); }

    @Override
    public String toString() {
        return
            "KeystoreMetadata\n" +
            "{\n" +
            "  outputKeystoreFilename='" + _outputKeystoreFilename + "\',\n" +
            "  baseKeystoreFilename='" + _baseKeystoreFilename + "\',\n" +
            "  keystorePassword='" + _keystorePassword + "\',\n" +
            "  certificates=\n" + _certificates.toString() + "\n" +
            "}\n";
    }
}
