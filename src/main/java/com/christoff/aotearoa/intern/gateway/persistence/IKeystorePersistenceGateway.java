package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.KeystoreMetadata;

import java.util.Map;

public interface IKeystorePersistenceGateway
{
    void persist(Map<String, KeystoreMetadata> keystores);
}
