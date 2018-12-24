package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.intern.gateway.metadata.CertificateMetadata;
import com.christoff.aotearoa.intern.gateway.metadata.KeystoreMetadata;
import com.christoff.aotearoa.intern.gateway.persistence.IKeystorePersistenceGateway;
import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

/***
 * Implementation need only implement
 *
 *   IKeystorePersistenceGateway.persist(Map<String, KeystoreMetadata> keystores)
 *
 *
 *   https://stackoverflow.com/questions/24137463/how-to-load-public-certificate-from-pem-file/24139603
 *
 *   https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#CertificateFactory
 *
 *   https://www.baeldung.com/java-keystore
 *
 */
public class KeystorePersistenceFileGateway implements IKeystorePersistenceGateway
{
    private String _outputDir;
    private PersistenceFileHelper _fileHelper = new PersistenceFileHelper();

    public KeystorePersistenceFileGateway(String outputDir) {
        _outputDir = outputDir;
    }

    @Override
    /***
     * Takes a map of resolved KeystoreMetadata (e.g.: values filled-in) and creates keystores
     * to output folder.
     */
    public void persist(Map<String,KeystoreMetadata> keystores)
    {
        for(KeystoreMetadata km : keystores.values())
        {
            // check whether we are building a new keystore, or adding to an existing one
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
        String outputFilename = FilenameUtils.normalize(_outputDir + "/" + km.getOutputKeystoreFilename());

        if(useExistingKeystore)
        {
            // load existing keystore
            String ksFilename = FilenameUtils.normalize(_outputDir + "/" + km.getBaseKeystoreFilename());
            ks = loadJKSKeystore(ksFilename, km.getKeystorePassword());
        }
        else {
            // create new keystore
            ks = createJKSKeystore(km.getKeystorePassword());
        }

        for(CertificateMetadata cm : km.getCertificates())
        {
            String alias = km.getAliasByCertRef(cm.getName());
            Certificate c = ks.getCertificate(alias);
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
        String certFilename = FilenameUtils.normalize(_outputDir + "/" + name);
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        FileInputStream is = new FileInputStream (certFilename);
        X509Certificate cer = (X509Certificate) fact.generateCertificate(is);

        return cer;
    }

    public static KeyStore createJKSKeystore(String password)
        throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException
    {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        char[] pwdArray = password.toCharArray();
        ks.load(null, pwdArray);
        return ks;
    }

    public static void saveKeystore(KeystoreMetadata km, KeyStore ks, String ksFilename)
        throws FileNotFoundException, KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException
    {
        ks.store(
                new FileOutputStream(ksFilename), km.getKeystorePassword().toCharArray());
    }

    public static KeyStore loadJKSKeystore(String keystoreFilename, String password)
        throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException
    {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream(keystoreFilename), password.toCharArray());
        return ks;
    }








    /*
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

        throw new UnsupportedOperationException("Keystore functionality not yet implemented");
    }
    */
}




