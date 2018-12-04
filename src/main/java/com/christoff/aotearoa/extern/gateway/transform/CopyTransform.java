package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import java.util.List;

public class CopyTransform implements ITransform
{
    @Override
    public String transform(List<String> inputList) { return inputList.get(0); }
}
