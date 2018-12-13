package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.ConfigException;

public class TemplateException extends ConfigException
{
    public TemplateException(String message) {
        super("TEMPLATES: " + message);
    }
}
