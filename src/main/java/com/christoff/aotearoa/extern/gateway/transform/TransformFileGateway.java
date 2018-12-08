package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import java.util.HashMap;
import java.util.Map;

public class TransformFileGateway implements ITransformGateway {
    
    private static final Map<String,ITransform> _transformMap = new HashMap<>();
    static {
        _transformMap.put("copy", new TransformCopy());
        _transformMap.put("encrypt", new TransformAESEncryptor());
        _transformMap.put("comma-separated", new TransformCommaSeparate());
    }
    
    @Override
    public ITransform get(String transformKey) {
        return _transformMap.get(transformKey);
    }
}
