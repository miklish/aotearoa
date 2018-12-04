package com.christoff.aotearoa.extern.gateway.metadata.local;

import com.christoff.aotearoa.extern.gateway.persistence.local.FileSystemHelper;
import com.christoff.aotearoa.intern.gateway.metadata.IVariableMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.util.*;

/***
    This class returns both the variable metadata and the values of variables
    required in config files
 */
public class VariableMetadataFileGateway implements IVariableMetadataGateway
{
    public static final String VARIABLES = "variables";
    
    private String _diffFilename;
    
    private IValueGateway _valueGateway;
    private ITransformGateway _transformGateway;
    
    FileSystemHelper _fileSysHelper;

    private Map<String, VariableMetadata> _allVarMetadata;
    private List<String> _allConfigSetNames;

    public VariableMetadataFileGateway(
        String diffFilename)
    {
        _diffFilename = diffFilename;
        _fileSysHelper = new FileSystemHelper();
    }

    public List<String> getAllConfigSetNames()
    {
        if(_allConfigSetNames == null) {
            Set<String> configSet = new HashSet<>();
            for (VariableMetadata v : _allVarMetadata.values())
                configSet.addAll(v.getProperty("files"));
            _allConfigSetNames = new LinkedList<>(configSet);
        }
        
        return _allConfigSetNames;
    }
    
    // get all config values
    public Map<String,VariableMetadata> getAllConfigMetadata()
    {
        if(_allVarMetadata != null) return _allVarMetadata;
        
        Map<String, Object> metadataMap =
            (Map<String, Object>) _fileSysHelper.getFileInfo(_diffFilename, true, false).map.get(VARIABLES);

        Map<String, VariableMetadata> allVarMetadata = new HashMap<>();
        for(Map.Entry<String,Object> varMetadataEntry : metadataMap.entrySet())
        {
            // get the key : the variable's name
            String varName = varMetadataEntry.getKey();

            // convert the value, and Object, from its native type (Map<String,Object>)
            // to Map<String,String> to ensure consistent single value-type for processing
            Map<String,List<String>> varMetadataPropertiesMap = new HashMap<>();

            for(Map.Entry<String,Object> e : ((Map<String,Object>) varMetadataEntry.getValue()).entrySet())
            {
                String key = e.getKey().trim().toLowerCase();
                
                List<String> val = new LinkedList<>();
                Object valueObj = e.getValue();
                if(valueObj instanceof List) {
                    for (Object o : (List) valueObj) {
                        if (o instanceof String)
                            val.add((String) o);
                        else
                            val.add(o.toString());
                    }
                }
                else if(valueObj instanceof String)
                    val.add((String) valueObj);
                else
                    val.add(valueObj.toString());
                
                varMetadataPropertiesMap.put(key, val);
            }
            VariableMetadata varMetadataClass = new VariableMetadata(varName, varMetadataPropertiesMap);
            allVarMetadata.put(varName, varMetadataClass);
        }
        return allVarMetadata;
    }

    public VariableMetadata getMetadata(String variableId)
    {
        if(_allVarMetadata == null)
            _allVarMetadata = getAllConfigMetadata();
        
        return _allVarMetadata.get(variableId);
    }
}
