package com.christoff.aotearoa.extern.gateway.transform;

import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import java.util.List;

public class CommaSeparateTransform implements ITransform
{
    @Override
    public String transform(List<String> inputList)
    {
        if(inputList == null || inputList.size() == 0)
            return "";

        int len = inputList.size();
        if(len == 1) return inputList.get(0);

        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len - 1; ++i)
            sb.append(inputList.get(i)).append(",");
        sb.append(inputList.get(len-1));
        
        return sb.toString();
    }
}
