package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import java.util.HashMap;
import java.util.Map;

public class TransformServerGateway implements ITransformGateway
{
    private static final Map<String,ITransform> _transformMap = new HashMap<>();
    static {
        _transformMap.put(ITransformGateway.COPY, new TransformCopy());

        // config-server does encryption server-side, so encrypt/decrypt transform does nothing locally
        _transformMap.put(ITransformGateway.ENCRYPT, new TransformCopy());
        _transformMap.put(ITransformGateway.DECRYPT, new TransformCopy());

        _transformMap.put(ITransformGateway.COMMA_SEPARATED, new TransformCommaSeparate());
    }
    
    @Override
    public ITransform get(String transformKey) {
        return _transformMap.get(transformKey);
    }
}
