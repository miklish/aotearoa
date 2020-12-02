package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class TemplateRegexResolver
{
    private Pattern _p;
    private Pattern _quote;
    private IPresenter _presenter;

    private static final String TOKEN_SEPARATOR = "\\|";
    private static final String PROPERTY_PAIR_SEPARATOR = ";";
    private static final String PROPERTY_KEYVAL_SEPARATOR = ":";
    private static final String QUOTE_TOKEN = "*";

    private static final String DEFAULT_TOKEN_REGEX = "\\$\\{(.*?)\\}";
    private static final String QUOTE_REGEX = "([\"'])(?:\\\\.|[^\\\\])*?\\1";
    
    public TemplateRegexResolver(IPresenter presenter)
    {
        _p = Pattern.compile(DEFAULT_TOKEN_REGEX);
        _quote = Pattern.compile(QUOTE_REGEX);
        _presenter = presenter;
    }
    
    public TemplateRegexResolver(IPresenter presenter, String pattern)
    {
        if(pattern != null && !pattern.trim().equals("")) {
            try {
                _p = Pattern.compile(pattern);
            } catch(PatternSyntaxException e) {
                throw new MetadataException("Custom token regex pattern " + pattern + " is invalid");
            }
        }
        else {
            _p = Pattern.compile(DEFAULT_TOKEN_REGEX);
        }

        _quote = Pattern.compile(QUOTE_REGEX);
        _presenter = presenter;
    }

    public String resolve(String templateName, String template, Map<String, Metadata> map)
    {
        Matcher m = _p.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (m.find())
        {
            String tokenName = parse(m.group(1)).getName();

            Metadata variableMetadata = map.get(tokenName);
            if(variableMetadata == null) {
                throw new MetadataException(
                    "Tag " + m.group(1) + " appears in template " + templateName + " but does not have any metadata");
            }

            m.appendReplacement(sb, variableMetadata.getTransformedVariableString());
            variableMetadata.setUsed();
        }

        return m.appendTail(sb).toString();
    }

    public Map<String, List<Metadata>> extractTemplateMetadata(String sourceContents)
        throws MetadataException
    {
        Map<String, List<Metadata>> map = new HashMap<>();

        Matcher m = _p.matcher(sourceContents);
        while (m.find())
        {
            String token = m.group(1);
            _presenter.tokenFound(token);

            Metadata metadata = parse(token);
            String tokenName = metadata.getName();

            if(map.containsKey(tokenName))
                map.get(tokenName).add(metadata);
            else {
                List<Metadata> list = new LinkedList<>();
                list.add(metadata);
                map.put(tokenName, list);
            }
        }

        return map;
    }

    private Metadata parse(String tokenWithQuotes)
    {
        if(tokenWithQuotes == null || tokenWithQuotes.trim().equals(""))
            return null;

        // replace quoted chunks with QUOTE_TOKEN1,...,QUOTE_TOKENn
        Quotes q = replaceQuote(tokenWithQuotes, QUOTE_TOKEN);
        String dequoted = q.dequoted;

        // get token name
        String[] tokenAndProps = dequoted.split(TOKEN_SEPARATOR);
        Metadata metadata = new Metadata(tokenAndProps[0].trim());
        if(tokenAndProps.length == 1) {
            _presenter.tokenHasNoProperties();
            return metadata;
        }

        // Parse out each of the property/value pairs
        String properties = tokenAndProps[1];
        String[] propertyPairList = properties.split(PROPERTY_PAIR_SEPARATOR);

        for(String propertyPairString : propertyPairList)
        {
            // parse each pair
            String[] propertyPairArray = propertyPairString.split(PROPERTY_KEYVAL_SEPARATOR);

            String key;
            String val;

            // default values do not have explicit keys
            if(propertyPairArray == null || propertyPairArray.length == 0) {
                continue;
            }
            if(propertyPairArray.length == 1) {
                key = "def";
                val = propertyPairArray[0].trim();
            }
            else {
                key = propertyPairArray[0].trim();
                val = propertyPairArray[1].trim();
            }

            val = val.startsWith(QUOTE_TOKEN) ? q.values.get(val) : val;

            if(key.equalsIgnoreCase("out") || key.equalsIgnoreCase("output")) {
                metadata.setProperty("output", val);
                _presenter.tokenHasProperty("output = " + val);
            }

            else if(key.equalsIgnoreCase("def") || key.equalsIgnoreCase("defaults")) {
                metadata.setProperty("defaults", val);
                _presenter.tokenHasProperty("defaults = " + val);
            }
        }

        return metadata;
    }

    private static class Quotes
    {
        public Map<String, String> values;
        public String dequoted;

        public Quotes(String s) {
            dequoted = s;
            values = new HashMap<>();
        }
    }

    /**
     * Replaces quoted String with the String r + i, for i = 0,...
     * @param token String to search for quote
     * @param prefix replacement prefix String
     *
     * @return the quotes that were found (in order), and the original String with the
     *         quotes replaced
     */
    private Quotes replaceQuote(String token, String prefix)
    {
        Quotes quotes = new Quotes(token);

        Matcher m = _quote.matcher(token);
        StringBuffer sb = new StringBuffer();

        for(int i = 0; m.find(); ++i) {
            String quoteKey = prefix + i;
            quotes.values.put(quoteKey, m.group());
            m.appendReplacement(sb, quoteKey);
        }

        quotes.dequoted = m.appendTail(sb).toString();
        return quotes;
    }
}
