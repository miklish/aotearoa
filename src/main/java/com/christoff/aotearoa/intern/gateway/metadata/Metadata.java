package com.christoff.aotearoa.intern.gateway.metadata;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.values.ValueException;

import java.util.*;


public class Metadata
{
    /** Metadata properties */
    public static final String MIN = "min";
    public static final String MAX = "max";
    public static final String OUTPUT = "output";
    public static final String TYPE = "type";
    public static final String REPLACEMENT_STRATEGY = "replacement-strategy";
    public static final String PROMPT_TEXT = "prompt-text";
    public static final String FILES = "files";
    public static final String DEFAULTS = "defaults";
    
    private String _varName;
    private Map<String,List<String>> _varPropertiesMap;
    private List<String> _values;
    private ITransform _transform;
    private boolean _isUsed = false;
    private boolean _defaultUsed = false;
    
    public Metadata(
        String varName,
        Map<String,List<String>> varPropertiesMap)
    {
        _varName = varName;
        _varPropertiesMap = varPropertiesMap;
    }

    /**
     * Builds a default varPropertiesMap
     *
     * @param varName
     */
    public Metadata(String varName)
    {
        Map<String,List<String>> varPropertiesMap = new HashMap<>();
        // output
        List<String> output = new LinkedList<>();
        output.add("copy");
        // min
        List<String> min = new LinkedList<>();
        min.add("1");
        // max
        List<String> max = new LinkedList<>();
        max.add("1");
        // defaults
        List<String> defaults = new LinkedList<>();
        // files
        List<String> files = new LinkedList<>();
        // type
        List<String> type = new LinkedList<>();
        type.add("string");
        // prompt-text
        List<String> promptText = new LinkedList<>();
        promptText.add("Please enter the value for " + varName);

        varPropertiesMap.put(OUTPUT, output);
        varPropertiesMap.put(MIN, min);
        varPropertiesMap.put(MAX, max);
        varPropertiesMap.put(DEFAULTS, defaults);
        varPropertiesMap.put(FILES, files);
        varPropertiesMap.put(TYPE, type);
        varPropertiesMap.put(PROMPT_TEXT, promptText);

        _varName = varName;
        _varPropertiesMap = varPropertiesMap;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(_varName, metadata._varName) &&
            Objects.equals(_varPropertiesMap, metadata._varPropertiesMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_varName, _varPropertiesMap);
    }

    public void setUsed() {
        _isUsed = true;
    }

    public boolean getUsed() { return _isUsed; }

    public void setDefaultUsed() { _defaultUsed = true; }

    public boolean getDefaultUsed() { return _defaultUsed; }

    public void setValues(List<Object> values) { _values = toStringList(_varName, values); }
    
    public void setTransformation(ITransform transform) {
        _transform = transform;
    }

    private static List<String> toStringList(String varName, List<Object> objValues)
    {
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

    public void setProperty(String key, List<String> value) {
        _varPropertiesMap.put(key, value);
    }

    /**
     * Convenience method to add a single String value
     *
     * @param key
     * @param value
     */
    public void setProperty(String key, String value) {
        List<String> valueList = new LinkedList<>();
        valueList.add(value);
        _varPropertiesMap.put(key, valueList);
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
