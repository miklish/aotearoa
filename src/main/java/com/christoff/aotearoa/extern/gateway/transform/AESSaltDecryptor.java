package com.christoff.aotearoa.extern.gateway.transform;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.christoff.aotearoa.ConfigException;

public class AESSaltDecryptor {
    private static String CRYPT_PREFIX = "CRYPT";
    private String secretKey;

    //private static final Logger logger = LoggerFactory.getLogger(AESSaltDecryptor.class);

    private static final int ITERATIONS = 65536;
    private static final int KEY_SIZE = 256;
    private static final String STRING_ENCODING = "UTF-8";
    private static final byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // cache the secret to void recreating instances for each decrypt call as all config files
    // will use the same salt per application.
    private Map<String, SecretKeySpec> secretMap = new ConcurrentHashMap<>();
    private Cipher cipher;
    IvParameterSpec ivSpec;

    public AESSaltDecryptor(String secretKey) throws ConfigException
    {
        if(secretKey == null || secretKey.length() == 0) {
            throw new ConfigException("Secret key is null or empty");
        }
        this.secretKey = secretKey;

        try {
            // CBC = Cipher Block chaining
            // PKCS5Padding Indicates that the keys are padded
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ivSpec = new IvParameterSpec(iv);
        } catch (Exception e) {
            throw new ConfigException("Unable to initialize decryptor: " + e.getMessage());
        }
    }

    public String decrypt(String input) {
        if (!input.startsWith(CRYPT_PREFIX)) {
            throw new RuntimeException("Unable to decrypt, input string does not start with 'CRYPT'.");
        }
        String[] parts = input.split(":");
        // need to make sure that the salt is in the secret text.
        if(parts.length != 3) {
            throw new RuntimeException("Unable to decrypt, input string is not formatted correctly with CRYPT:salt:hash");
        }

        try {
            byte[] salt = fromHex(parts[1]);
            byte[] hash = fromHex(parts[2]);
            // try to get the secret from the cache first.
            SecretKeySpec secret = secretMap.get(parts[1]);
            if(secret == null) {
                KeySpec spec = new PBEKeySpec(getPassword(), salt, ITERATIONS, KEY_SIZE);
                /* Derive the key, given password and salt. */
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                SecretKey tmp = factory.generateSecret(spec);
                secret = new SecretKeySpec(tmp.getEncoded(), "AES");
                secretMap.put(parts[1], secret);
            }
            cipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
            return new String(cipher.doFinal(hash), STRING_ENCODING);
        } catch (Exception e) {
            throw new RuntimeException("Unable to decrypt: Incorrect key or incorrect ciphertext.", e);
        }
    }

    private char[] getPassword() {
        return secretKey.toCharArray();
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i < bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
}
