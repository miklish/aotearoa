package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.extern.gateway.transform.TransformAESDecryptor;
import com.christoff.aotearoa.intern.gateway.metadata.CertificateMetadata;
import com.christoff.aotearoa.intern.gateway.metadata.KeystoreMetadata;
import com.christoff.aotearoa.intern.gateway.persistence.IKeystorePersistenceGateway;

import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

/***
 *   Documentation/Resources on Keystore API
 *
 *     https://stackoverflow.com/questions/24137463/how-to-load-public-certificate-from-pem-file/24139603
 *     https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#CertificateFactory
 *     https://www.baeldung.com/java-keystore
 *
 */
public class KeystorePersistenceFileGateway implements IKeystorePersistenceGateway
{
    private String _keystoreMetadataFile;
    private String _outputDir;
    private PersistenceFileHelper _fileHelper = new PersistenceFileHelper();
    private static TransformAESDecryptor _decrypt = new TransformAESDecryptor();

    public KeystorePersistenceFileGateway(String keystoreMetadatafile, String outputDir)
    {
        _keystoreMetadataFile = keystoreMetadatafile;
        _outputDir = outputDir;
    }

    @Override
    /***
     * Takes a map of resolved KeystoreMetadata (e.g.: values filled-in), creates keystores,
     * and saves to output folder.
     */
    public void persist(Map<String,KeystoreMetadata> keystores)
    {
        for(KeystoreMetadata km : keystores.values())
        {
            try {
                processKeystore(km);
            } catch (Exception e) {
                throw new
                    ConfigException("An error was encountered while attempting to construct your keystores\n" +
                    e.getMessage());
            }
        }
    }

    private void processKeystore(KeystoreMetadata km) throws Exception
    {
        boolean useExistingKeystore = km.getBaseKeystoreFilename() != null;

        KeyStore ks;
        String outputFilename = PersistenceFileHelper.cleanFilename(_outputDir + "/" + km.getOutputKeystoreFilename());
    
        // check whether we are building a new keystore, or adding to an existing one
        if(useExistingKeystore)
        {
            // get key data base dir
            File ksMetaFile = new File(_keystoreMetadataFile);
            String ksMetaDir = ksMetaFile.getParentFile().getCanonicalPath();

            // load existing keystore
            String ksFilename = PersistenceFileHelper.cleanFilename(ksMetaDir + "/" + km.getBaseKeystoreFilename());
            ks = loadJKSKeystore(ksFilename, _decrypt.decrypt(km.getKeystorePassword()));
        }
        else
            // create new keystore
            ks = createJKSKeystore(_decrypt.decrypt(km.getKeystorePassword()));

        for(CertificateMetadata cm : km.getCertificates())
        {
            String alias = km.getAliasByCertRef(cm.getName());
            importCertificate(km, ks, alias);
        }

        saveKeystore(km, ks, outputFilename);
    }

    public void importCertificate(KeystoreMetadata km, KeyStore ks, String alias) throws Exception
    {
        CertificateMetadata cm = km.getCertByAlias(alias);
        Certificate cert = loadCertificate(cm.getFilename());
        ks.setCertificateEntry(alias, cert);
    }

    public Certificate loadCertificate(String name) throws Exception
    {
        String certFilename = PersistenceFileHelper.cleanFilename(_outputDir + "/" + name);
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream (certFilename);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);

        return cer;
    }

    public static KeyStore createJKSKeystore(String password)
        throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException
    {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] pwdArray = _decrypt.decrypt(password).toCharArray();
        ks.load(null, pwdArray);
        return ks;
    }

    public static void saveKeystore(KeystoreMetadata km, KeyStore ks, String ksFilename)
        throws FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException
    {
        ks.store(new FileOutputStream(ksFilename), _decrypt.decrypt(km.getKeystorePassword()).toCharArray());
    }

    public static KeyStore loadJKSKeystore(String keystoreFilename, String password)
        throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException
    {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystoreFilename), _decrypt.decrypt(password).toCharArray());
        return ks;
    }
}




