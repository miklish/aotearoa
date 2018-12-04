package com.christoff.aotearoa.extern.gateway.transform.configserver;

import com.christoff.aotearoa.extern.gateway.transform.CommaSeparateTransform;
import com.christoff.aotearoa.extern.gateway.transform.CopyTransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import java.util.HashMap;
import java.util.Map;

public class TransformServerGateway implements ITransformGateway {
    
    private static final Map<String,ITransform> _transformMap = new HashMap<>();
    static {
        _transformMap.put("copy", new CopyTransform());

        // config-server does encryption server-side, so encrypt-transform does nothing locally
        _transformMap.put("encrypt", new CopyTransform());

        _transformMap.put("comma-separated", new CommaSeparateTransform());
    }
    
    @Override
    public ITransform get(String transformKey) {
        return _transformMap.get(transformKey);
    }
}
