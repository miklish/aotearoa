package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateResolverFunction;
import java.util.Map;

public class PersistenceServerGateway implements IPersistenceGateway
{
    public PersistenceServerGateway() {
        throw new UnsupportedOperationException("config-server persistence not supported yet");
    }

    @Override
    public Object persistValues(TemplateResolverFunction resolver, Map<String, Metadata> allVarMetadata) {
        throw new UnsupportedOperationException("config-server persistence not supported yet");
    }
}
