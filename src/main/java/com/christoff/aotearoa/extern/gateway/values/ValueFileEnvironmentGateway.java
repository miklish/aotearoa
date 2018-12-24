package com.christoff.aotearoa.extern.gateway.values;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ValueFileEnvironmentGateway implements IValueGateway
{
    private IValueGateway _valueFileGateway;
    private IValueGateway _valueEnvGateway;
    
    public ValueFileEnvironmentGateway(IValueGateway valueFileGateway, IValueGateway valueEnvGateway) {
        _valueEnvGateway = valueEnvGateway;
        _valueFileGateway = valueFileGateway;
    }

    @Override
    public List<Object> get(Metadata vm)
    {
        return _valueEnvGateway.get(vm).size() > 0 ?    // check whether environment has a value
            _valueEnvGateway.get(vm) :                  //   yes: use the environment valaue
            _valueFileGateway.get(vm);                  //   no : use file value
    }

    @Override
    public void setMetadata(Map<String, Metadata> allVarMetadata) {}
}
