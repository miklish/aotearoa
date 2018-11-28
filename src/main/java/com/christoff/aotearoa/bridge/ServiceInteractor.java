package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.intern.VarMetadata;
import com.christoff.aotearoa.intern.gateway.IServiceConfigDataGateway;
import com.christoff.aotearoa.intern.gateway.IServiceValueGateway;
import com.christoff.aotearoa.intern.gateway.ITransformGateway;
import com.christoff.aotearoa.intern.view.IServicePresenter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceInteractor
{
    public static final String USE = "use";
    public static final String VARIABLES = "variables";
    public static final String FILES = "files";

    private IServiceConfigDataGateway _configGateway;
    private IServiceValueGateway _valueGateway;
    private IServicePresenter _presenter;
    private ITransformGateway _transformGateway;

    private Map<String, Object> _diffMap = null;
    private ServiceRequest _rq = null;

    public ServiceInteractor(
            IServiceConfigDataGateway configGateway,
            IServiceValueGateway valueGateway,
            IServicePresenter presenter,
            ITransformGateway transformGateway
    ) {
        _configGateway = configGateway;
        _valueGateway = valueGateway;
        _presenter = presenter;
        _transformGateway = transformGateway;
    }

    public ServiceResponse exec(ServiceRequest request)
    {
        _rq = request;                                      // save request
        _diffMap = _configGateway.get(request.configId);    // Load in the diff file
        
        Map<String,Object> useMap = getUseMap();
        Map<String,VarMetadata> variablesMap = (Map<String, VarMetadata>) _diffMap.get(VARIABLES);

        // dispatch sections of the diff file to different methods/classes
        
        return new ServiceResponse("Success", "SUCCESS");
    }
    
    // returns map of populated VarMeta data objects
    private Map<String,VarMetadata> getVarMetadataMap()
    {
        /*
        Map<String,Object> varMap = (Map<String, Object>) _diffMap.get(VARIABLES);
        Map<String,VarMetadata> allVarMeta = new HashMap<>();
        
        for(Map.Entry<String,Object> varMetadata : varMap.entrySet()) {
            ITransformGateway tx = _transformGateway.get(varMetadata.get("comma-separated"));
            VarMetadata var = new VarMetadata(varEntry.getKey(), (Map<String, Object> varMap, ))
        }
        */
        
        return null;
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
            System.out.println("use-file: baseId= " + _rq.baseId + " dataId=" + useFileKey);

            // load the source data
            Map<String, Object> srcRoot = _configGateway.get(_rq.baseId, useFileKey);

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
}
