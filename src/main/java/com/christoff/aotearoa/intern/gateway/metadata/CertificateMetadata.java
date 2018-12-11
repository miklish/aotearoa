package com.christoff.aotearoa.intern.gateway.metadata;

/*
tableau-webserver-cert:                                   # this is an internal reference name for the keystore
  format: pem                                             # certificate format
  prompt-text: Tableau webserver public certificate       # prompt text
  file-name: tableauUAT.pem                               # filename of keystore
*/
public class CertificateMetadata
{
    private String _format;
    private String _promptText;
    private String _filename;
    private String _name;
    
    public String getName() {
        return _name;
    }
    
    public void setName(String name) {
        _name = name;
    }
    
    public String getFormat() {
        return _format;
    }
    
    public void setFormat(String format) {
        _format = format;
    }
    
    public String getPromptText() {
        return _promptText;
    }
    
    public void setPromptText(String promptText) {
        _promptText = promptText;
    }
    
    public String getFilename() {
        return _filename;
    }
    
    public void setFilename(String filename) {
        _filename = filename;
    }
    
    @Override
    public String toString() {
        return "CertificateMetadata{" +
            "_format='" + _format + '\'' +
            ", _promptText='" + _promptText + '\'' +
            ", _filename='" + _filename + '\'' +
            ", _name='" + _name + '\'' +
            '}';
    }
}
