package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateResolver;

import java.util.Map;

public class PersistenceServerGateway implements IPersistenceGateway
{
    public PersistenceServerGateway() {
        throw new UnsupportedOperationException("config-server persistence not supported yet");
    }

    @Override
    public void persistValues(TemplateResolver resolver, Map<String, VariableMetadata> allVarMetadata) {
    }
}
