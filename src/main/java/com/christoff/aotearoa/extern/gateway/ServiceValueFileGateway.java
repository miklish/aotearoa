package com.christoff.aotearoa.extern.gateway;

import com.christoff.aotearoa.intern.gateway.ConfigIOException;
import com.christoff.aotearoa.intern.gateway.IServiceValueGateway;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ServiceValueFileGateway implements IServiceValueGateway
{
    private Map<String,Object> _valueMap;
    private YamlHelper _yamlHelper;

    public ServiceValueFileGateway(String valueFile) {

        _yamlHelper = new YamlHelper();
        try {
            _valueMap = _yamlHelper.loadYaml(valueFile);
        } catch (IOException e) {
            throw new ConfigIOException(e.getMessage());
        }
    }

    @Override
    public List<String> get(String key) {
        return (List<String>) _valueMap.get(key);
    }
}
