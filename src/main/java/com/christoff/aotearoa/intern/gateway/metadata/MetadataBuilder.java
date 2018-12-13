package com.christoff.aotearoa.intern.gateway.metadata;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MetadataBuilder
{
    // build Metadata classes and inject values and transforms
    public static Map<String,Metadata> build(Map<String, Object> variablesMetadataMapObjects)
    {
        // check there are some variable metadata instances to convert
        if(variablesMetadataMapObjects.size() == 0) throw new MetadataException("No variable metadata found");

        // build Metadata map
        Map<String, Metadata> variablesMetadataMap = new HashMap<>();
        
        for(Map.Entry<String,Object> varMetadataEntry : variablesMetadataMapObjects.entrySet())
        {
            // get the key : the variable's name
            String varName = varMetadataEntry.getKey();
        
            // convert the value, and Object, from its native type (Map<String,Object>)
            // to Map<String,String> to ensure consistent single value-type for processing
            Map<String, List<String>> varMetadataPropertiesMap = new HashMap<>();
        
            for(Map.Entry<String,Object> e : ((Map<String,Object>) varMetadataEntry.getValue()).entrySet()) {
                String key = e.getKey().trim().toLowerCase();
            
                List<String> val = new LinkedList<>();
                Object valueObj = e.getValue();
            
                if(valueObj == null)
                    throw new MetadataException(
                        "No metadata found in " + key + " section for tag " + varName);
            
                if(valueObj instanceof List)
                {
                    List valueList = (List) valueObj;
                
                    if(valueList.isEmpty())
                        throw new MetadataException(
                            "No metadata found in " + key + " section for tag " + varName);
                
                    for (Object o : valueList) {
                    
                        if(o == null)
                            throw new MetadataException(
                                "Missing metadata for tag " + varName + " in " + key + " section");
                    
                        if (o instanceof String)
                            val.add((String) o);
                        else
                            val.add(o.toString());
                    }
                }
                else if(valueObj instanceof String)
                    val.add((String) valueObj);
                else
                    val.add(valueObj.toString());
            
                varMetadataPropertiesMap.put(key, val);
            }
            
            Metadata varMetadataClass = new Metadata(varName, varMetadataPropertiesMap);
            variablesMetadataMap.put(varName, varMetadataClass);
        }
        
        return variablesMetadataMap;
    }
}
