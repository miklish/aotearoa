package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import java.io.Reader;
import java.util.Map;

@FunctionalInterface
public interface TemplateResolverFunction
{
    String resolve(String templateName, Reader reader, Map<String, Metadata> map);
}
