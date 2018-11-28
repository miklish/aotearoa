package com.christoff.aotearoa.intern;

import com.christoff.aotearoa.intern.gateway.ITransform;

import java.util.List;
import java.util.Map;

public class VarMetadata
{
    private String _varName;
    private Map<String,Object> _map;
    private ITransform _tx;
    
    public VarMetadata(String varName, Map<String,Object> map, ITransform tx) {
        _varName = varName;
        _map = map;
        _tx = tx;
    }
    
    public String getVarName() {
        return _varName;
    }
    
    public String getString(String key) {
        return (String) _map.get(key);
    }
    
    public String getTransformedString(String key) {
        return _tx.transform((String) _map.get(key));
    }
    
    public List<String> getList(String key) {
        return (List<String>) _map.get(key);
    }
    
    public String getProcessedList(String key) {
        return (String) _tx.transform((List<String>) _map.get(key));
    }
}
