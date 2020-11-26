package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateRegexResolver
{
    private Pattern _p;
    private Pattern _quote;
    
    public TemplateRegexResolver()
    {
        _p = Pattern.compile("\\$\\{(.*?)\\}");
        _quote = Pattern.compile("([\"'])(?:\\\\.|[^\\\\])*?\\1");

        //_p = Pattern.compile("\\{\\{(.*?)\\}\\}");
        //_quote = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
    }
    
    public TemplateRegexResolver(String pattern) {
        _p = Pattern.compile(pattern);
    }
    
    public String resolve(String templateName, String template, Map<String, Metadata> map)
    {
        Matcher m = _p.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (m.find())
        {
            Metadata variableMetadata = map.get(m.group(1));
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
