package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import java.util.List;

public class TransformAESEncryptor implements ITransform
{
    AESSaltEncryptor encryptor;

    public static final String DEFAULT_SYMMETRIC_KEY = "light";
    public static final String CRYPT_PREFIX = "CRYPT";

    public TransformAESEncryptor(String symmetricKey)
    {
        if(symmetricKey == null || symmetricKey.length() == 0) {
            symmetricKey = TransformAESEncryptor.DEFAULT_SYMMETRIC_KEY;

            System.out.println(
                "WARNING: No secret key provided for AES decryption!\n" +
                "Using the well-known default key: this is a security risk!\n"
            );
        }

        this.encryptor = new AESSaltEncryptor(symmetricKey);
    }
    
    public String transform(List<String> inputList) throws RuntimeException {
        return transform(inputList.get(0));
    }
        
    /**
     * Encrypt given input string
     *
     * @param input
     * @return
     * @throws RuntimeException
     */
    private String transform(String input) throws RuntimeException {
        if(input == null) return null;
        return encryptor.encrypt(input);
    }
}
