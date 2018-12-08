package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import java.util.Map;

@FunctionalInterface
public interface TemplateResolverFunction
{
    String resolve(String templateName, String template, Map<String, Metadata> map);
}
