package com.christoff.aotearoa.extern.gateway.metadata.local;

import com.christoff.aotearoa.extern.gateway.persistence.local.FileSystemHelper;
import com.christoff.aotearoa.intern.gateway.metadata.IVariableMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/***
    This class returns both the variable metadata and the values of variables
    required in config files
 */
public class VariableMetadataFileGateway implements IVariableMetadataGateway
{
    public static final String VARIABLES = "variables";
    private String _metadataFilename;
    private FileSystemHelper _fileSysHelper;
    private Map<String, VariableMetadata> _allVarMetadata;

    public VariableMetadataFileGateway(String metadataFilename) {
        _metadataFilename = metadataFilename;
        _fileSysHelper = new FileSystemHelper();
        _allVarMetadata = initAllConfigMetadata();
    }
    
    public VariableMetadata getMetadata(String variableId) {
        return _allVarMetadata.get(variableId);
    }
    
    // get all config values
    public Map<String,VariableMetadata> getAllConfigMetadata() {
        return _allVarMetadata;
    }

    // initialize all config values
    private Map<String,VariableMetadata> initAllConfigMetadata()
        throws MetadataException
    {
        // read in the entire metadata file
        Map<String, Object> allConfigDataMap =
            _fileSysHelper.getFileInfo(_metadataFilename, true, false).map;

        // check that it contains data
        if(allConfigDataMap == null || allConfigDataMap.size() == 0)
            throw new MetadataException("No data found in " + _metadataFilename);



        // get the variable section of the metadata configuration (keys are Objects)
        Map<String, Object> variablesMetadataMapObjects = (Map<String, Object>) allConfigDataMap.get(VARIABLES);

        // check that the variables section contains data
        if(variablesMetadataMapObjects == null || variablesMetadataMapObjects.size() == 0)
            throw new MetadataException("No variable metadata found in " + _metadataFilename);



        // check there are some variable metadata instances to convert
        if(variablesMetadataMapObjects.size() == 0)
            throw new MetadataException("No variable metadata found in " + _metadataFilename);

        // convert keys to the String type
        Map<String, VariableMetadata> variablesMetadataMap = new HashMap<>();

        for(Map.Entry<String,Object> varMetadataEntry : variablesMetadataMapObjects.entrySet())
        {
            // get the key : the variable's name
            String varName = varMetadataEntry.getKey();

            // convert the value, and Object, from its native type (Map<String,Object>)
            // to Map<String,String> to ensure consistent single value-type for processing
            Map<String,List<String>> varMetadataPropertiesMap = new HashMap<>();

            for(Map.Entry<String,Object> e : ((Map<String,Object>) varMetadataEntry.getValue()).entrySet()) {
                String key = e.getKey().trim().toLowerCase();
                
                List<String> val = new LinkedList<>();
                Object valueObj = e.getValue();
                
                if(valueObj == null)
                    throw new MetadataException(
                        "No metadata found in " + key + " section for tag " + varName);
                
                if(valueObj instanceof List)
                {
                    List valueList = (List) valueObj;
                    
                    if(valueList.isEmpty())
                        throw new MetadataException(
                            "No metadata found in " + key + " section for tag " + varName);
                    
                    for (Object o : valueList) {
    
                        if(o == null)
                            throw new MetadataException(
                                "Missing metadata for tag " + varName + " in " + key + " section");
                        
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
            variablesMetadataMap.put(varName, varMetadataClass);
        }
        return variablesMetadataMap;
    }
}
