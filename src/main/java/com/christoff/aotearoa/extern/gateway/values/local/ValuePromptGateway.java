package com.christoff.aotearoa.extern.gateway.values.local;

import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ValuePromptGateway implements IValueGateway
{
    @Override
    public List<Object> get(String configValueId) {
        throw new NotImplementedException();
    }
}
