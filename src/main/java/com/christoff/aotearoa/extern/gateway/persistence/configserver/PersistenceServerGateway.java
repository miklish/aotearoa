package com.christoff.aotearoa.extern.gateway.persistence.configserver;

import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;

import java.util.Map;

public class PersistenceServerGateway implements IPersistenceGateway
{
    public PersistenceServerGateway() {
        throw new UnsupportedOperationException("config-server persistence not supported yet");
    }

    @Override
    public void persistValues(Map<String, VariableMetadata> allVarMetadata) {
    }
}
