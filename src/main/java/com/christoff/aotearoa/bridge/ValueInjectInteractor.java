package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.intern.gateway.metadata.*;
import com.christoff.aotearoa.intern.gateway.persistence.IKeystorePersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateRegexResolver;
import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import java.util.*;

public class ValueInjectInteractor
{
    private IMetadataGateway _metadataGateway;
    private IKeystoreMetadataGateway _keystoreMetadataGateway;
    private IPersistenceGateway _persistenceGateway;
    private IKeystorePersistenceGateway _keystorePersistenceGateway;
    private IPresenter _presenter;
    private IValueGateway _valueGateway;
    private ITransformGateway _transformGateway;
    private ValueInjectRequest _rq = null;
    
    public ValueInjectInteractor(
        IMetadataGateway metadataGateway,
        IKeystoreMetadataGateway keystoreMetadataGateway,
        IPersistenceGateway persistenceGateway,
        IKeystorePersistenceGateway keystorePersistenceGateway,
        IValueGateway valueGateway,
        ITransformGateway transformGateway,
        IPresenter presenter
    ) {
        _metadataGateway = metadataGateway;
        _keystoreMetadataGateway = keystoreMetadataGateway;
        _persistenceGateway = persistenceGateway;
        _keystorePersistenceGateway = keystorePersistenceGateway;
        _presenter = presenter;
        _valueGateway = valueGateway;
        _transformGateway = transformGateway;
    }
    
    public ValueInjectResponse exec(ValueInjectRequest request)
        throws MetadataException
    {
        _rq = request;

        
        // TODO: decide how to handle 'use-values' in a generic way
        //Map<String,Object> useMap = getUseMap();

        
        // TODO: Add code to handle certs
        // Create a 'certs' section in the _values.yml file
        // https://www.baeldung.com/java-keystore

        
        // Gather all values and variable metadata
        Map<String, Metadata> allVarMetadata = MetadataBuilder.getAllMetadata(_metadataGateway);
        
        // Validate metadata
        for(Metadata vm : allVarMetadata.values()) MetadataValidator.validateMetadata(vm);
        
        // provide all metadata to ValueGateway (some implementations require all metadata at once)
        _valueGateway.setMetadata(allVarMetadata);

        // Add concrete values and transforms to metadata
        for (Metadata varMeta : allVarMetadata.values())
        {
            // Set the variable values
            List<Object> values = _valueGateway.get(varMeta);
            if(values == null || values.isEmpty())
                throw new MetadataException(
                    "There is no metadata for the value with tag " + varMeta.getName());
            varMeta.setValues(values);

            
            // Set the transform for the variable
            
            // - get the transform name
            List<String> transformNames = varMeta.getProperty(Metadata.OUTPUT);
            if(transformNames == null || transformNames.isEmpty())
                throw new MetadataException(
                    "No transformations specified in metadata for tag " + varMeta.getName());
            
            // - get the transform
            ITransform transform = _transformGateway.get(transformNames.get(0));
            if(transform == null)
                throw new MetadataException(
                    "Transformation " + transformNames.get(0) +
                    "is not a valid transform for metadata tag " + varMeta.getName());
            
            // set the transform
            varMeta.setTransformation(transform);
        }

        // Resolve and persist the templates
        _presenter.persistingValuesBegin();
        _persistenceGateway.persistValues(
            TemplateRegexResolver::resolve, allVarMetadata);
        _presenter.persistingValuesEnd();
    
        // Check if any metadata went unused
        for(Metadata vm : allVarMetadata.values())
            if(!vm.getUsed()) _presenter.tagDefinedNotUsed(vm.getName());
        
        
        
        // Load keystore metadata (if it exists)
        KeystoreMetadataBuilder.buildCertificateMetadata(
            _keystoreMetadataGateway.getCertificateMap(),
            _keystoreMetadataGateway.getKeystoreMap());
        
        
        
        
        return new ValueInjectResponse("Success", "SUCCESS");
    }
}




// Do not delete code below -- contains code for 'use' feature
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
}
*/
