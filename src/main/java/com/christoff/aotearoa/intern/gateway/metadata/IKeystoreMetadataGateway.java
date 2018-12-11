package com.christoff.aotearoa.intern.gateway.metadata;

import java.util.List;
import java.util.Map;

public interface IKeystoreMetadataGateway
{
    List<Map<String, Object>> getKeystoreMap();
    Map<String,Object> getCertificateMap();
}
