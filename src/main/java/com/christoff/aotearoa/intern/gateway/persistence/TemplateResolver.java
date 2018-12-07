package com.christoff.aotearoa.intern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateResolver
{
    private static Pattern p = Pattern.compile("\\{\\{(.*?)\\}\\}");
    
    public String resolve(String templateName, String template, Map<String, VariableMetadata> map)
    {
        Matcher m = p.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (m.find())
        {
            VariableMetadata variableMetadata = map.get(m.group(1));
            if(variableMetadata == null) {
                throw new MetadataException(
                    "Tag " + m.group(1) + " appears in template " + templateName + " but does not have any metadata");
            }

            m.appendReplacement(sb, variableMetadata.getTransformedVariableString());
            variableMetadata.setUsed();
        }

        return m.appendTail(sb).toString();
    }
}
