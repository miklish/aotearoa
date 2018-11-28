package com.christoff.aotearoa.extern.gateway;

import com.christoff.aotearoa.intern.gateway.ITransform;
import com.christoff.aotearoa.intern.gateway.ITransformGateway;

import java.util.HashMap;
import java.util.Map;

public class DefaultTransformGateway implements ITransformGateway {
    
    private static final Map<String,ITransform> _transformMap = new HashMap<>();
    static {
        _transformMap.put("copy", new CopyTransform());
        _transformMap.put("encrypt", new AESEncryptorTransform());
        _transformMap.put("comma-separated", new CommaSeparateTransform());
    }
    
    @Override
    public ITransform get(String transformKey) {
        return _transformMap.get(transformKey);
    }
}
