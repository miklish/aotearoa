package com.christoff.aotearoa.extern.gateway.values.local;

import com.christoff.aotearoa.extern.gateway.YamlHelper;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataIOException;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.values.ValueException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ValueFileGateway implements IValueGateway
{
    private Map<String,Object> _valueMap;
    private YamlHelper _yamlHelper;

    public ValueFileGateway(String valueFile) {

        _yamlHelper = new YamlHelper();
        try {
            _valueMap = _yamlHelper.loadYaml(valueFile);
        } catch (IOException e) {
            throw new MetadataIOException(e.getMessage());
        }
    }

    @Override
    public List<Object> get(String configValueId)
    {
        if(!(_valueMap.get(configValueId) instanceof List))
            throw new ValueException("Expected a list of values for " + configValueId);
        
        return (List<Object>) _valueMap.get(configValueId);
    }
}
