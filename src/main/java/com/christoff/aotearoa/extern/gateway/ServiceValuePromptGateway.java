package com.christoff.aotearoa.extern.gateway;

import com.christoff.aotearoa.intern.gateway.IServiceValueGateway;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class ServiceValuePromptGateway implements IServiceValueGateway
{
    @Override
    public List<String> get(String configValId) {
        throw new NotImplementedException();
    }
}
