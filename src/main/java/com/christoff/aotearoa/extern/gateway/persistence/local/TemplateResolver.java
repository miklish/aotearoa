package com.christoff.aotearoa.extern.gateway.persistence.local;

import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateResolver
{
    private static Pattern p = Pattern.compile("\\{(.*?)\\}");
    
    public static String resolve(String template, Map<String, VariableMetadata> map) {
        Matcher m = p.matcher(template);
        StringBuffer sb = new StringBuffer();
    
        while (m.find())
            //m.appendReplacement(sb, map.get(m.group(1)).getVariableString());
            m.appendReplacement(sb, map.get(m.group(1)).getTransformedVariableString());
        m.appendTail(sb);
        System.out.println("\n-------------\n" + sb.toString() + "\n------------------\n");
        return m.toString();
    }
}
