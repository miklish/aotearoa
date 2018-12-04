package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import java.util.Map;

public interface IPersistenceGateway
{
    void persistValues(Map<String,VariableMetadata> allVarMetadata);
}
