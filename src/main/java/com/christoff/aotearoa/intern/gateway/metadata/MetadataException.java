package com.christoff.aotearoa.intern.gateway.metadata;

import com.christoff.aotearoa.ConfigException;

public class MetadataException extends ConfigException
{
    public MetadataException(String message) {
        super("METADATA: " + message);
    }
}
