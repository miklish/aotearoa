package com.christoff.aotearoa.intern.gateway.metadata;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.values.ValueException;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VariableMetadata
{
    /** Metadata properties */
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String OUTPUT = "output";
    public static final String TYPE = "type";
    public static final String REPLACEMENT_STRATEGY = "replacement-strategy";
    public static final String PROMPT_TEXT = "prompt-text";
    public static final String FILES = "files";
    
    private String _varName;
    private Map<String,List<String>> _varPropertiesMap;
    private List<String> _values;
    private ITransform _transform;
    
    public VariableMetadata(
        String varName,
        Map<String,List<String>> varPropertiesMap)
    {
        _varName = varName;
        _varPropertiesMap = varPropertiesMap;
    }
    
    public void setValues(List<Object> values) {
        _values = toStringList(_varName, values);
    }
    
    public void setTransformation(ITransform transform) {
        _transform = transform;
    }

    private static List<String> toStringList(String varName, List<Object> objValues) {
        List<String> values = new LinkedList<>();
        for(Object val : objValues)
        {
            if(val == null)
                throw new ValueException("No values found for tag " + varName);
                
            if (val instanceof String)
                values.add((String) val);
            else
                values.add(val.toString());
        }
        return values;
    }

    public String getName() {
        return _varName;
    }
    
    public List<String> getProperty(String key) {
        return _varPropertiesMap.get(key);
    }
    
    public ITransform transform() {
        return _transform;
    }
    
    public String getVariableString() {
        return _values.get(0);
    }
    
    public List<String> getVariableListString() {
        return _values;
    }
    
    public String getTransformedVariableString() {
        return _transform.transform(_values);
    }
    
    public int getVariableInteger() {
        return Integer.parseInt(_values.get(0));
    }
}
