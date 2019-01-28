package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import java.util.Map;

public interface IPersistenceGateway
{
    /***
     *
     * @param resolver regex resolver
     * @param allVarMetadata contains data on metadata
     * @return may return null, or can return resolved YAML as standard Map/List structure
     */
    Object persistValues(TemplateResolverFunction resolver, Map<String, Metadata> allVarMetadata);
}
