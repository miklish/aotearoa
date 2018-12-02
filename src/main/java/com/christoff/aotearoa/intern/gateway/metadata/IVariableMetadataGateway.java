package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IVariableMetadataGateway
{
    public Map<String,VariableMetadata> getAllConfigMetadata();
    public VariableMetadata getMetadata(String variableId);
}
