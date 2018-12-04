package com.christoff.aotearoa.extern.gateway.persistence.local;

import com.christoff.aotearoa.intern.gateway.metadata.MetadataFormatException;
import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateResolver
{
    private static Pattern p = Pattern.compile("\\{(.*?)\\}");
    
    public static String resolve(String template, Map<String, VariableMetadata> map)
    {
        Matcher m = p.matcher(template);
        StringBuffer sb = new StringBuffer();
    
        String tag;
        while (m.find())
        {
            VariableMetadata variableMetadata = map.get(m.group(1));
            if(variableMetadata == null) {
                throw new MetadataFormatException(
                    "Tag " + m.group(1) + " appears in template but does not have any metadata");
            }

            m.appendReplacement(sb, variableMetadata.getTransformedVariableString());
        }
        m.appendTail(sb);

        //System.out.println("-------------\n" + sb.toString() + "\n------------------\n");

        return sb.toString();
    }
}
