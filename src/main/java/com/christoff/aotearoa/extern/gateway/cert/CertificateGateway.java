package com.christoff.aotearoa.extern.gateway.cert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificateGateway
{
    private String _outputDir;
    
    public CertificateGateway(String outputDir) {
        _outputDir = outputDir;
    }
    
    public static KeyStore createJKSKeystore(String password)
    {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    
        char[] pwdArray = password.toCharArray();
        try {
            ks.load(null, pwdArray);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return ks;
    }
    
    public static void saveKeystore(KeyStore ks, String keystoreName, String password)
    {
        try (FileOutputStream fos = new FileOutputStream(keystoreName)) {
            ks.store(fos, password.toCharArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
    
    public static KeyStore loadJKSKeystore(String keystoreFilename, String password)
    {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        
        try {
            ks.load(new FileInputStream(keystoreFilename), password.toCharArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        
        return ks;
    }
    
    public static void importAsymetricPrivateKey(
        KeyStore ks,
        PrivateKey privateKey,
        String privateKeyAlias,
        String privateKeyPassword,
        X509Certificate[] certificateChain)
    {
        try {
            ks.setKeyEntry(privateKeyAlias, privateKey, privateKeyPassword.toCharArray(), certificateChain);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
