package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.ConfigException;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import static java.lang.System.exit;

/**
 * This is a new encryptor utility to encrypt the secret with stronger implementation.
 * Instead of using a static salt, we are generating a salt for each invocation to make
 * dictionary attack harder.
 */
public class AESSaltEncryptor {
    private static final String CRYPT_PREFIX = "CRYPT";
    private String secretKey;

    private static final int ITERATIONS = 65536;
    private static final int KEY_SIZE = 256;
    private static final String STRING_ENCODING = "UTF-8";
    private static final byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static byte[] salt;
    private SecretKeySpec secret;
    private Cipher cipher;
    private IvParameterSpec ivSpec;

    public AESSaltEncryptor(String secretKey)
    {
        if(secretKey == null || secretKey.length() == 0) {
            throw new ConfigException("Secret key is null or empty");
        }
        this.secretKey = secretKey;
    }

    /**
     * Encrypt given input string
     *
     * @param input
     * @return
     * @throws RuntimeException
     */
    public String encrypt(String input) throws RuntimeException
    {
        refreshEncryptionParameters();

        try {
            byte[] inputBytes = input.getBytes(STRING_ENCODING);
            cipher.init(Cipher.ENCRYPT_MODE, secret, ivSpec);
            byte[] out = cipher.doFinal(inputBytes);
            return CRYPT_PREFIX + ":" + toHex(salt) + ":" +toHex(out);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch (BadPaddingException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to encrypt", e);
        } catch(InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Unable to encrypt", e);
        }
    }

    private void refreshEncryptionParameters() {
        try {
            /* Derive the key, given password and salt. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            salt = getSalt();
            KeySpec spec = new PBEKeySpec(secretKey.toCharArray(), salt, ITERATIONS, KEY_SIZE);
            SecretKey tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            // CBC = Cipher Block chaining
            // PKCS5Padding Indicates that the keys are padded
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            ivSpec = new IvParameterSpec(iv);
        } catch (Exception e) {
            throw new ConfigException("Unable to initialize: " + e.getMessage());
        }
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);

        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }
}
