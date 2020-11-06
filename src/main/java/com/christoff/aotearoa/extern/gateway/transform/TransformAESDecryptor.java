package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.intern.gateway.transform.ITransform;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;

public class TransformAESDecryptor implements ITransform
{
    private static final int ITERATIONS = 65536;
    private static final String STRING_ENCODING = "UTF-8";

    /**
     * If we user Key size of 256 we will get java.security.InvalidKeyException:
     * Illegal key size or default parameters , Unless we configure Java
     * Cryptography Extension 128
     */
    private static final int KEY_SIZE = 128;
    private static final byte[] SALT = { (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0 };
    private SecretKeySpec secret;
    private Cipher cipher;
    private Base64.Decoder base64Decoder = Base64.getDecoder();
    private String symmetricKey;

    public TransformAESDecryptor(String symmetricKey)
    {
        this.symmetricKey = symmetricKey == null || symmetricKey.length() == 0 ?
            TransformAESEncryptor.DEFAULT_SYMMETRIC_KEY : symmetricKey;
        
        try
        {
            /* Derive the key, given password and salt. */
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec;

            spec = new PBEKeySpec(this.symmetricKey.toCharArray(), SALT, ITERATIONS, KEY_SIZE);
            SecretKey tmp = factory.generateSecret(spec);
            secret = new SecretKeySpec(tmp.getEncoded(), "AES");

            // CBC = Cipher Block chaining
            // PKCS5Padding Indicates that the keys are padded
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        } catch (Exception e) {
            throw new RuntimeException("Unable to initialize TransformAESDecryptor", e);
        }
    }
    
    public String transform(List<String> inputList) {
        return transform(inputList.get(0));
    }

    public String transform(String input)
    {
        if (!input.startsWith(TransformAESEncryptor.CRYPT_PREFIX))  return input;

        try
        {
            String encodedValue = input.substring(6, input.length());
            byte[] data = base64Decoder.decode(encodedValue);
            int keylen = KEY_SIZE / 8;
            byte[] iv = new byte[keylen];
            System.arraycopy(data, 0, iv, 0, keylen);
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
            return new String(cipher.doFinal(data, keylen, data.length - keylen), STRING_ENCODING);
        }
        catch (Exception e) {
            throw new ConfigException("Unable to decrypt keystore password");
        }
    }
}