package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.intern.gateway.metadata.IVariableMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.view.IServicePresenter;

import java.util.*;

public class ValueInjectInteractor
{
    public static final String USE = "use";
    public static final String VARIABLES = "variables";
    public static final String FILES = "files";

    private IVariableMetadataGateway _metadataGateway;
    private IPersistenceGateway _persistenceGateway;
    private IServicePresenter _presenter;
    private IValueGateway _valueGateway;
    private ITransformGateway _transformGateway;

    private ValueInjectRequest _rq = null;

    public ValueInjectInteractor(
            IVariableMetadataGateway metadataGateway,
            IPersistenceGateway persistenceGateway,
            IValueGateway valueGateway,
            ITransformGateway transformGateway,
            IServicePresenter presenter
    ) {
        _metadataGateway = metadataGateway;
        _persistenceGateway = persistenceGateway;
        _presenter = presenter;
        _valueGateway = valueGateway;
        _transformGateway = transformGateway;
    }

    public ValueInjectResponse exec(ValueInjectRequest request)
    {
        _rq = request;

        // TODO: decide how to handle 'use-values' in a generic way
        //Map<String,Object> useMap = getUseMap();

        // gather all values and variable metadata
        Map<String,VariableMetadata> allVarMetadata = _metadataGateway.getAllConfigMetadata();
        
        // add values and transforms to metadata
        for(VariableMetadata varMeta : allVarMetadata.values()) {
            varMeta.setValues(_valueGateway.get(varMeta.getName()));
            varMeta.setTransformation(_transformGateway.get(varMeta.getProperty("output").get(0)));
        }

        _persistenceGateway.persistValues(allVarMetadata);
        
        return new ValueInjectResponse("Success", "SUCCESS");
    }

    /*
    private List<String> getTags(String s)
    {
        List<String> tags = new ArrayList<String>();
        
        Pattern regex = Pattern.compile("\\{(.*?)\\}");
        Matcher regexMatcher = regex.matcher(s);
        
        // fetch groups
        while(regexMatcher.find())
            tags.add(regexMatcher.group(1));
        
        return tags;
    }
    
    private Map<String,Object> getUseMap()
    {
        // All tag names must be unique across all config files of the service

        Map<String,Object> allFileValues = new HashMap<String,Object>();

        // get root of 'use' section in diff
        Map<String, Object> useMapInfo = (Map<String, Object>) _diffMap.get(USE);
        Set<String> useFiles = useMapInfo.keySet();

        // loop through the files in the 'use' section
        for(String useFileKey : useFiles)
        {
            // Root of the 'pointers' section for a single file in the 'use' section
            System.out.println("current use-file : " + useFileKey);
            Map<String,Object> pointerRoot = (Map<String,Object>) useMapInfo.get(useFileKey);
            System.out.println("use-file: templateDir= " + _rq.templateDir + " dataId=" + useFileKey);

            // load the source data
            Map<String, Object> srcRoot = _configGateway.get(_rq.templateDir, useFileKey);

            // now we pair (a) the root of the data-location pointers + (b) root of the source data
            Map<String,Object> fileResolutions = resolveUseFileVariables(pointerRoot, srcRoot);

            allFileValues.putAll(fileResolutions);
        }

        return allFileValues;
    }

    private Map<String,Object> resolveUseFileVariables(
        Map<String,Object> pointerRoot, Map<String,Object> srcRoot)
    {
        final Map<String,Object> fileMap = new HashMap<String,Object>();
        yamlWalker(pointerRoot, srcRoot, (searchKey, searchTag, srcValue) -> fileMap.put(searchTag, srcValue));
        return fileMap;
    }
    
    @FunctionalInterface
    private interface YamlVisitor { void visit(String searchKey, String searchTag, Object srcValue); }
    
    private void yamlWalker(Map<String,Object> pointer, Map<String,Object> src, YamlVisitor visitor)
    {
        // invariant: pointer and srcValue are at same level in respective hierarchies
        
        if(pointer == null) return;
        
        for(Map.Entry<String,Object> pEntry : pointer.entrySet())
        {
            // assumption: All Entry values are either a List or  a Map
            
            if(pEntry.getValue() instanceof String)
            {
                // In pointer tree, node is a leaf iff node is a String
                // Therefore Entry key is the search-key, and Entry value is the search-tag
                // Note: The node in a src tree that corresponds to a pointer tree leaf, may not itself be
                // a leaf (e.g.: it may be a list or Map)
                String searchKey = pEntry.getKey();
                String searchTag = (String) pEntry.getValue();
                
                // find the value for the same key in src -- note: src value may not be a String
                Object srcValue = src.get(searchKey);
                
                // provide to visitor for processing
                visitor.visit(searchKey, searchTag, srcValue);
            }
            else
            {
                // bring src down a level to match pEntry.value to match pointer's level (and maintain invariant)
                Map<String,Object> newPointer = (Map<String,Object>) pEntry.getValue();
                Map<String,Object> newSrc = (Map<String,Object>) src.get(pEntry.getKey());
                
                yamlWalker(newPointer, newSrc, visitor);
            }
        }
    }
    */
}
