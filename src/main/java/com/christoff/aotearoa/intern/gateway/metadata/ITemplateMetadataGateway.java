package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.List;
import java.util.Map;


public interface ITemplateMetadataGateway
{
    Map<String, Map<String, List<Metadata>>> getMetadataFromTemplates();
}
