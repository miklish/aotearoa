package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.extern.gateway.persistence.TemplateRegexResolver;
import com.christoff.aotearoa.intern.gateway.metadata.*;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateIOException;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
//import java.util.LinkedList;
import java.util.List;
import java.util.Map;
//import java.util.regex.Matcher;


public class TemplateMetadataFileGateway implements ITemplateMetadataGateway
{
    private static final boolean IS_RECURSIVE = false;

//    private Pattern _p;
//    private Pattern _quote;

    private IPresenter _presenter;
    private TemplateRegexResolver _regexResolver;
    private String _templateFolder;
    private String[] _templateExtensions;
    private PersistenceFileHelper _fileSysHelper;

//    private static final String TOKEN_SEPARATOR = "\\|";
//    private static final String PROPERTY_PAIR_SEPARATOR = ";";
//    private static final String PROPERTY_KEYVAL_SEPARATOR = ":";
//    private static final String QUOTE_TOKEN = "*";

    public TemplateMetadataFileGateway(
        IPresenter presenter,
        TemplateRegexResolver regexResolver,
//        String pattern,
        String templateFolder,
        List<String> templateExtensions
    ) {
        _presenter = presenter;
        _regexResolver = regexResolver;

//        if(pattern != null && !pattern.trim().equals("")) {
//            try {
//                _p = Pattern.compile(pattern);
//            } catch(PatternSyntaxException e) {
//                throw new MetadataException("Custom token regex pattern " + pattern + " is invalid");
//            }
//        }
//        else {
//            _p = Pattern.compile("\\$\\{(.*?)\\}");
//        }
//        _quote = Pattern.compile("([\"'])(?:\\\\.|[^\\\\])*?\\1");

        _fileSysHelper = new PersistenceFileHelper();

        _templateFolder = templateFolder;
        _templateExtensions = new String[templateExtensions.size()];
        _templateExtensions = templateExtensions.toArray(_templateExtensions);
    }

    /***
     * templateName -> Map[ tokenName, List[Metadata] ]
     *
     * @return
     * @throws TemplateIOException
     * @throws MetadataIOException
     */
    public Map<String, Map<String, List<Metadata>>> getMetadataFromTemplates()
        throws TemplateIOException, MetadataIOException
    {
        _presenter.collectingMetadataFromTemplates(_templateFolder);

        Map<String, Map<String, List<Metadata>>> templateMetadataMap = new HashMap<>();

        // get template files
        File templateFolderFile = new File(_templateFolder);
        List<File> files = (List<File>) FileUtils.listFiles(templateFolderFile, _templateExtensions, IS_RECURSIVE);

        // loop through each template file
        for (File file : files)
        {
            // read the template files as Strings
            String templateName = file.getName();
            String templateContents;

            _presenter.loadingTemplate(templateName);

            // read in the template file
            templateContents = _fileSysHelper.getFileInfo(file, false, true).string;

            // get the map of metadata objects from the template
            Map<String, List<Metadata>> newMetadataObjects = _regexResolver.extractTemplateMetadata(templateContents);
            templateMetadataMap.put(templateName, newMetadataObjects);
        }

        return templateMetadataMap;
    }

//    public Map<String, List<Metadata>> extractTemplateMetadata(String sourceContents)
//        throws MetadataException
//    {
//        Map<String, List<Metadata>> map = new HashMap<>();
//
//        Matcher m = _p.matcher(sourceContents);
//        while (m.find())
//        {
//            String token = m.group(1);
//            _presenter.tokenFound(token);
//
//            Metadata metadata = parse(token);
//            String tokenName = metadata.getName();
//
//            if(map.containsKey(tokenName))
//                map.get(tokenName).add(metadata);
//            else {
//                List<Metadata> list = new LinkedList<>();
//                list.add(metadata);
//                map.put(tokenName, list);
//            }
//        }
//
//        return map;
//    }
//
//    public Metadata parse(String tokenWithQuotes)
//    {
//        if(tokenWithQuotes == null || tokenWithQuotes.trim().equals(""))
//            return null;
//
//        // replace quoted chunks with QUOTE_TOKEN1,...,QUOTE_TOKENn
//        Quotes q = replaceQuote(tokenWithQuotes, QUOTE_TOKEN);
//        String dequoted = q.dequoted;
//
//        // get token name
//        String[] tokenAndProps = dequoted.split(TOKEN_SEPARATOR);
//        Metadata metadata = new Metadata(tokenAndProps[0].trim());
//        if(tokenAndProps.length == 1) {
//            _presenter.tokenHasNoProperties();
//            return metadata;
//        }
//
//        // Parse out each of the property/value pairs
//        String properties = tokenAndProps[1];
//        String[] propertyPairList = properties.split(PROPERTY_PAIR_SEPARATOR);
//
//        for(String propertyPairString : propertyPairList)
//        {
//            // parse each pair
//            String[] propertyPairArray = propertyPairString.split(PROPERTY_KEYVAL_SEPARATOR);
//            String key = propertyPairArray[0];
//            String val = propertyPairArray[1];
//
//            val = val.startsWith(QUOTE_TOKEN) ? q.values.get(val) : val;
//
//            if(key.equalsIgnoreCase("out") || key.equalsIgnoreCase("output")) {
//                metadata.setProperty("output", val);
//                _presenter.tokenHasProperty("output = " + val);
//            }
//
//            else if(key.equalsIgnoreCase("def") || key.equalsIgnoreCase("defaults")) {
//                metadata.setProperty("defaults", val);
//                _presenter.tokenHasProperty("defaults = " + val);
//            }
//        }
//
//        return metadata;
//    }
//
//    public static class Quotes
//    {
//        public Map<String, String> values;
//        public String dequoted;
//
//        public Quotes(String s) {
//            dequoted = s;
//            values = new HashMap<>();
//        }
//    }
//
//    /**
//     * Replaces quoted String with the String r + i, for i = 0,...
//     * @param token String to search for quote
//     * @param prefix replacement prefix String
//     *
//     * @return the quotes that were found (in order), and the original String with the
//     *         quotes replaced
//     */
//    public Quotes replaceQuote(String token, String prefix)
//    {
//        Quotes quotes = new Quotes(token);
//
//        Matcher m = _quote.matcher(token);
//        StringBuffer sb = new StringBuffer();
//
//        for(int i = 0; m.find(); ++i) {
//            String quoteKey = prefix + i;
//            quotes.values.put(quoteKey, m.group());
//            m.appendReplacement(sb, quoteKey);
//        }
//
//        quotes.dequoted = m.appendTail(sb).toString();
//        return quotes;
//    }
}
