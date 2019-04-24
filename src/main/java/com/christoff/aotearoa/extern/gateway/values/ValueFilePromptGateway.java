package com.christoff.aotearoa.extern.gateway.values;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.util.List;
import java.util.Map;

public class ValueFilePromptGateway implements IValueGateway
{
    private IValueGateway _valueFileGateway;
    private IValueGateway _valuePromptGateway;

    public ValueFilePromptGateway(IValueGateway valueFileGateway, IValueGateway valuePromptGateway) {
        _valuePromptGateway = valuePromptGateway;
        _valueFileGateway = valueFileGateway;
    }

    @Override
    public boolean exists(Metadata vm) { return true; }

    @Override
    public List<Object> get(Metadata vm)
    {
        return _valueFileGateway.exists(vm) ?   // check whether value-file has a value
            _valueFileGateway.get(vm) :                 //   yes: use the value-file value
            _valuePromptGateway.get(vm);                //   no : use the prompt
    }

    @Override
    public void setMetadata(Map<String, Metadata> allVarMetadata) {}
}
