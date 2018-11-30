package com.christoff.aotearoa.intern;

import com.christoff.aotearoa.intern.gateway.ITransform;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VariableMetadata
{
    private String _varName;
    private Map<String,String> _varPropertiesMap;
    private List<String> _var;
    private List<ITransform> _transforms;
    
    public VariableMetadata(String varName, Map<String,String> varPropertiesMap) {
        _varName = varName;
        _varPropertiesMap = varPropertiesMap;
    }
    
    public void addTransform(ITransform tx) {
        _transforms.add(tx);
    }
    
    public void setVar(List<Object> var) {
        // convert List to Strings
        List<String> varString = new LinkedList<>();
        for(Object o : var) {
            if (o instanceof Integer)
                varString.add(Integer.toString(10));
            else
                varString.add((String) o);
        }
        _var = varString;
    }
    
    public String getName() {
        return _varName;
    }
    
    public String getProperty(String key) {
        return _varPropertiesMap.get(key);
    }
    
    public List<ITransform> transform() {
        return _transforms;
    }
    
    public String getVariableString() {
        return _var.get(0);
    }
    
    public List<String> getVariableListString() {
        return _var;
    }
    
    public String getTransformedVariableString() {
        return _transforms.get(0).transform(_var);
    }
    
    public int getVariableInteger() {
        return Integer.parseInt(_var.get(0));
    }
}
