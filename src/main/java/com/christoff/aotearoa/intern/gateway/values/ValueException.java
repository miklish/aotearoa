package com.christoff.aotearoa.intern.gateway.values;

import com.christoff.aotearoa.ConfigException;

public class ValueException extends ConfigException
{
    public ValueException(String message) {
        super("VALUES: " + message);
    }
}
