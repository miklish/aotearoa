package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.KeystoreMetadata;
import com.christoff.aotearoa.intern.gateway.persistence.IKeystorePersistenceGateway;
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
import java.util.Map;

/***
 * Implementation need only implement
 *
 *   IKeystorePersistenceGateway.persist(Map<String, KeystoreMetadata> keystores)
 *
 */
public class KeystorePersistenceFileGateway implements IKeystorePersistenceGateway
{
    private String _outputDir;
    
    public KeystorePersistenceFileGateway(String outputDir) {
        _outputDir = outputDir;
    }
    
    @Override
    public void persist(Map<String, KeystoreMetadata> keystores) {
    }
    
    public static KeyStore createJKSKeystore(String password)
    {
        /*
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
        */

        throw new UnsupportedOperationException("Keystore functionality not yet implemented");
    }
    
    public static void saveKeystore(KeyStore ks, String keystoreName, String password)
    {
        /*
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
        */

        throw new UnsupportedOperationException("Keystore functionality not yet implemented");
    }
    
    public static KeyStore loadJKSKeystore(String keystoreFilename, String password)
    {
        /*
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
        */

        throw new UnsupportedOperationException("Keystore functionality not yet implemented");
    }
    
    public static void importAsymetricPrivateKey(
        KeyStore ks,
        PrivateKey privateKey,
        String privateKeyAlias,
        String privateKeyPassword,
        X509Certificate[] certificateChain)
    {
        /*
        try {
            ks.setKeyEntry(privateKeyAlias, privateKey, privateKeyPassword.toCharArray(), certificateChain);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        */

        throw new UnsupportedOperationException("Keystore functionality not yet implemented");
    }
}
