package com.christoff.aotearoa.extern.gateway.values;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ValueEnvironmentGateway implements IValueGateway
{
    private Map<String,List<Object>> _values = new HashMap<>();

    @Override
    public List<Object> get(Metadata vm)
    {
        String varName = vm.getName();

        if(!_values.containsKey(varName))
        {
            String val = System.getenv(varName);
            List<Object> valList = new LinkedList<>();
            valList.add(val);
            _values.put(varName, valList);
        }

        return _values.get(varName);
    }

    @Override
    public void setMetadata(Map<String, Metadata> allVarMetadata) {}
}
