package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import java.util.HashMap;
import java.util.Map;

public class TransformFileGateway implements ITransformGateway
{
    private Map<String,ITransform> _transformMap;
    
    public TransformFileGateway(String symmetricKey) {
        _transformMap = new HashMap<>();
        _transformMap.put(ITransformGateway.COPY, new TransformCopy());
        _transformMap.put(ITransformGateway.ENCRYPT, new TransformAESEncryptor(symmetricKey));
        _transformMap.put(ITransformGateway.COMMA_SEPARATED, new TransformCommaSeparate());
        _transformMap.put(ITransformGateway.DECRYPT, new TransformAESDecryptor(symmetricKey));
    }
    
    @Override
    public ITransform get(String transformKey) {
        return _transformMap.get(transformKey);
    }
}
