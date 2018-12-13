package com.christoff.aotearoa.intern.gateway.metadata;

import com.christoff.aotearoa.intern.gateway.values.ValueException;
import java.util.List;

public class MetadataValidator
{
    public static boolean validateMetadata(Metadata vm)
    {
        // Inspect variable metadata
        
        List<String> sMin = vm.getProperty(Metadata.MIN);
        errorCheck(vm.getName(), Metadata.MIN, sMin);
        Integer min;
        try {
            min = Integer.parseInt(sMin.get(0));
        } catch(NumberFormatException e) {
            throw new MetadataException(
                "Min value for tag " + vm.getName() + " is not a number");
        }
        
        List<String> sMax = vm.getProperty(Metadata.MAX);
        errorCheck(vm.getName(), Metadata.MAX, sMax);
        Integer max;
        if (sMax.get(0).trim().toLowerCase().equals("inf"))
            max = Integer.MAX_VALUE;
        else
            try {
                max = Integer.parseInt(sMax.get(0));
            } catch(NumberFormatException e) {
                throw new MetadataException(
                    "Max value for tag " + vm.getName() + " is not a number");
            }
        
        if(min < 0 || min > max)
            throw new MetadataException(
                "Min value " + min + " and Max value " + max + " for tag " + vm.getName() + " are invalid");
        
        errorCheck(vm.getName(), Metadata.PROMPT_TEXT, vm.getProperty(Metadata.PROMPT_TEXT));
        errorCheck(vm.getName(), Metadata.TYPE, vm.getProperty(Metadata.TYPE));
        
        return true;
    }
    
    private static void errorCheck(String varName, String paramName, List<String> metaProperty)
        throws ValueException
    {
        if (metaProperty == null || metaProperty.size() == 0 || !metaProperty.stream().allMatch(s -> s != null)) {
            String msg = "No value found for property " + paramName + " in metadata for tag " + varName;
            throw new MetadataException(msg);
        }
    }
}
