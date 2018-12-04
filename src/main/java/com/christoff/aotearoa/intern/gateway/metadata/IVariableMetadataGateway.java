package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.Map;

public interface IVariableMetadataGateway
{
    public Map<String,VariableMetadata> getAllConfigMetadata();
    public VariableMetadata getMetadata(String variableId);
}
