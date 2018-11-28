package com.christoff.aotearoa.extern.gateway;

import com.christoff.aotearoa.intern.gateway.ITransform;

import java.util.List;

public class CopyTransform implements ITransform
{
    @Override
    public String transform(String input) { return input; }
    
    @Override
    public String transform(List<String> inputList) { return inputList.get(0); }
}
