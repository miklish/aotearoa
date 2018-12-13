package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.ConfigException;

public class TemplateIOException extends ConfigException
{
    public TemplateIOException(String message) {
        super("TEMPLATES: " + message);
    }
}

