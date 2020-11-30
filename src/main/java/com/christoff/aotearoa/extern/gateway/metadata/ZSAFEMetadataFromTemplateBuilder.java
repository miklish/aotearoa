package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.intern.gateway.metadata.IMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataIOException;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateIOException;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class ZSAFEMetadataFromTemplateBuilder
{
    private static final String VARIABLES = "variables";
    private static final boolean IS_RECURSIVE = false;

    private Pattern _p;
    private Pattern _quote;

    private IPresenter _presenter;
    private String _templateFolder;
    private String[] _templateExtensions;
    private String _metadataFilename;
    private String _valuesFilename;
    private PersistenceFileHelper _fileSysHelper;

    private static final String TOKEN_SEPARATOR = "\\|";
    private static final String PROPERTY_PAIR_SEPARATOR = ";";
    private static final String PROPERTY_KEYVAL_SEPARATOR = ":";
    private static final String QUOTE_TOKEN = "*";

    public ZSAFEMetadataFromTemplateBuilder(
        IPresenter presenter,
        String pattern,
        String templateFolder,
        List<String> templateExtensions,
        String metadataFilename,
        String valuesFilename
    ) {
        _presenter = presenter;
        if(pattern != null && !pattern.trim().equals("")) {
            try {
                _p = Pattern.compile(pattern);
            } catch(PatternSyntaxException e) {
                throw new MetadataException("Custom token regex pattern " + pattern + " is invalid");
            }
        }
        else {
            _p = Pattern.compile("\\$\\{(.*?)\\}");
        }
        _quote = Pattern.compile("([\"'])(?:\\\\.|[^\\\\])*?\\1");
        _fileSysHelper = new PersistenceFileHelper();

        _templateFolder = templateFolder;
        _templateExtensions = new String[templateExtensions.size()];
        _templateExtensions = templateExtensions.toArray(_templateExtensions);
        _metadataFilename = metadataFilename;
        _valuesFilename = valuesFilename;
    }

    // initialize all config values
    public Map<String, Object> getMetadata()
        throws MetadataException, MetadataIOException
    {
        /**
         * Rules:
         *   If one or more results exist for the same token:
         *   1. If there is Template Metadata -- use this first, otherwise
         *   2. If there is Metadata File Metadata -- use this second, otherwise
         *   3. Use Default (Value File) Metadata
         */

        // collect all result maps
        Map<String, Metadata> templateFile = null;
        Map<String, Metadata> metadataFile = getMetadataFromMetadataFile();
        Map<String, Metadata> valueFile;


        // collect all token names across all results

        // read in the entire metadata file
        Map<String, Object> allConfigDataMap =
            _fileSysHelper.getFileInfo(_metadataFilename, true, false).map;

        // check that it contains data
        if(allConfigDataMap == null || allConfigDataMap.size() == 0)
            throw new MetadataException("No data found in " + _metadataFilename);

        return (Map<String, Object>) allConfigDataMap.get(VARIABLES);
    }

    public Map<String, Metadata> getMetadataFromMetadataFile()
    {
        // read in the entire metadata file
        Map<String, Object> metadataFromMetadataFile =
            _fileSysHelper.getFileInfo(_metadataFilename, true, false).map;

        // check that it contains data
        if(metadataFromMetadataFile == null || metadataFromMetadataFile.size() == 0)
            metadataFromMetadataFile = new HashMap<>();

        return (Map<String, Metadata>) metadataFromMetadataFile.get(VARIABLES);
    }

    public Map<String, Metadata> collectMetadataFromValues()
        throws MetadataException, MetadataIOException
    {
        File valueFile = new File(_valuesFilename);

        // open the template file as a String
        Map<String, Object> vars = _fileSysHelper.getFileInfo(valueFile, true, false).map;

        // check that it contains data
        if(vars == null || vars.size() == 0)
            vars = new HashMap<>();

        // load up a new Map with default metadata for values
        Map<String, Metadata> metadataFromValues = new HashMap<>();
        for(String token : vars.keySet())
            metadataFromValues.put(token, new Metadata(token));

        return metadataFromValues;
    }

    public Map<String, Metadata> collectMetadataFromTemplates()
        throws TemplateIOException, MetadataIOException
    {
        _presenter.collectingMetadataFromTemplates(_templateFolder);

        Map<String, Metadata> mergedMap = new HashMap<>();

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
            Map<String, Metadata> newMetadataObjects = resolve(templateName, templateContents);

            // merge
            if(newMetadataObjects.size() == 0)
                _presenter.noTokensFoundInTemplate(templateName);
            else
                _presenter.mergingTokenFromTemplate(templateName);

            for(Metadata newMetadata : newMetadataObjects.values())
                metadataMergeOne(mergedMap, newMetadata);
        }

        return mergedMap;
    }

    public Map<String, Metadata> resolve(String sourceName, String sourceContents)
        throws MetadataException
    {
        Matcher m = _p.matcher(sourceContents);

        Map<String, Metadata> map = new HashMap<>();
        while (m.find())
        {
            String token = m.group(1);
            _presenter.tokenFound(token);
            Metadata metadata = parse(token);
            String tokenName = metadata.getName();

            metadataMergeOne(map, metadata);
        }

        return map;
    }

    /***
     * Merges two Maps of Metadata together, while checking for conflicts
     *
     * @param map1
     * @param map2
     *
     * @throws MetadataException
     */
    public void metadataMergeMaps(Map<String, Metadata> map1, Map<String, Metadata> map2)
        throws MetadataException
    {
        for(Metadata map2Value : map2.values())
            metadataMergeOne(map1, map2Value);
    }

    /***
     * Merges a metadata object into a metadata map, as long as there is no conflict
     *
     * @param map
     * @param newMetadata
     * @throws MetadataException
     */
    public void metadataMergeOne(Map<String, Metadata> map, Metadata newMetadata)
        throws MetadataException
    {
        // check if we already read this metadata
        String tokenName = newMetadata.getName();
        if(map.containsKey(tokenName))
        {
            // check if there is a conflict
            Metadata oldMetadata = map.get(tokenName);
            if(!newMetadata.equals(oldMetadata))
                throw new MetadataException("Conflicting metadata for token " + tokenName);

            _presenter.tokenAlreadyExists(tokenName);
        }
        else {
            _presenter.tokenAdded(tokenName);
            map.put(tokenName, newMetadata);
        }
    }

    public Metadata parse(String tokenWithQuotes)
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
            String key = propertyPairArray[0];
            String val = propertyPairArray[1];

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

    public static class Quotes
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
    public Quotes replaceQuote(String token, String prefix)
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
