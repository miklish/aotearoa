package com.christoff.aotearoa.intern.gateway.metadata;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.util.List;
import java.util.Map;

public class MetadataInjector
{
    private ITransformGateway _transformGateway;
    private IValueGateway _valueGateway;
    
    public MetadataInjector(IValueGateway valueGateway, ITransformGateway transformGateway)
    {
        _valueGateway = valueGateway;
        _transformGateway = transformGateway;
    }
    
    public Map<String, Metadata> inject(Map<String,Metadata> variablesMetadataMap)
        throws MetadataException
    {
        // Set value for Metadata
        
        for (Metadata varMeta : variablesMetadataMap.values())
        {
            // Set the variable values
            if(!_valueGateway.exists(varMeta)) {
                System.out.println("WARNING: No Value found for Metadata tag " + varMeta.getName());
                continue;
            }

            List<Object> values = _valueGateway.get(varMeta);
            if(values == null || values.isEmpty())
                throw new MetadataException(
                    "There is no metadata for the value with tag " + varMeta.getName());
            varMeta.setValues(values);
            
            
            // Set transform for Metadata
            
            // - get the transform name
            List<String> transformNames = varMeta.getProperty(Metadata.OUTPUT);
            if(transformNames == null || transformNames.isEmpty())
                throw new MetadataException(
                    "No transformations specified in metadata for tag " + varMeta.getName());
            
            // - get the transform
            ITransform transform = _transformGateway.get(transformNames.get(0));
            if(transform == null)
                throw new MetadataException(
                    "Transformation " + transformNames.get(0) +
                        "is not a valid transform for metadata tag " + varMeta.getName());
            
            // set the transform
            varMeta.setTransformation(transform);

        }
        
        return variablesMetadataMap;
    }
}
