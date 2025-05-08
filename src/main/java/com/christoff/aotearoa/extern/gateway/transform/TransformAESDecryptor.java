package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import java.util.List;

public class TransformAESDecryptor implements ITransform {
    private final AESSaltDecryptor decryptor;

    public TransformAESDecryptor(String symmetricKey) throws ConfigException {
        if(symmetricKey == null || symmetricKey.length() == 0) {
            symmetricKey = TransformAESEncryptor.DEFAULT_SYMMETRIC_KEY;

            System.out.println(
                "WARNING: No secret key provided for AES decryption!\n" +
                "Using the well-known default key: this is a security risk!\n"
            );
        }

        this.decryptor = new AESSaltDecryptor(symmetricKey);
    }
    
    public String transform(List<String> inputList) {
        return transform(inputList.get(0));
    }

    public String transform(String input) {
        if (!input.startsWith(TransformAESEncryptor.CRYPT_PREFIX))
            return input;

        return decryptor.decrypt(input);
    }
}
