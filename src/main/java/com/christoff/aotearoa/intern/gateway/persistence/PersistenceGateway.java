package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;

public interface PersistenceGateway
{
    void persistValue(VariableMetadata value);
}
