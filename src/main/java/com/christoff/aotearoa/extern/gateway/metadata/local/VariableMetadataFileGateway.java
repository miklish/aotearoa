package com.christoff.aotearoa.extern.gateway.metadata.local;

import com.christoff.aotearoa.extern.gateway.persistence.local.FileSystemHelper;
import com.christoff.aotearoa.intern.gateway.metadata.IVariableMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
    This class returns both the variable metadata and the values of variables
    required in config files
 */
public class VariableMetadataFileGateway implements IVariableMetadataGateway
{
    private String _diffFilename;
    private IValueGateway _valueGateway;
    private ITransformGateway _transformGateway;
    public static final String VARIABLES = "variables";
    FileSystemHelper _fileSysHelper;
    private Map<String, VariableMetadata> _allVarMetadata;

    public VariableMetadataFileGateway(
        String diffFilename,
        IValueGateway valueGateway,
        ITransformGateway transformGateway)
    {
        _diffFilename = diffFilename;
        _valueGateway = valueGateway;
        _transformGateway = transformGateway;
        _fileSysHelper = new FileSystemHelper();
        _allVarMetadata = getAllConfigMetadata();
    }

    // get all config values
    public Map<String,VariableMetadata> getAllConfigMetadata() {
        return _allVarMetadata;
    }

    // get all config values
    private Map<String,VariableMetadata> allConfigMetadata()
    {
        Map<String, Object> metadataMap =
            (Map<String, Object>) _fileSysHelper.getFileInfo(_diffFilename, true).map.get(VARIABLES);

        Map<String, VariableMetadata> allVarMetadata = new HashMap<>();
        for(Map.Entry<String,Object> varMetadataEntry : metadataMap.entrySet())
        {
            // get the key : the variable's name
            String varName = varMetadataEntry.getKey();

            // convert the value, and Object, from its native type (Map<String,Object>)
            // to Map<String,String> to ensure consistent single value-type for processing
            Map<String,String> varMetadataPropertiesMap = new HashMap<>();

            for(Map.Entry<String,Object> e : ((Map<String,Object>) varMetadataEntry.getValue()).entrySet())
            {
                String key = e.getKey().trim().toLowerCase();
                String val = e.getValue().toString().trim().toLowerCase();
                if(val.startsWith("[") && val.endsWith("]"))
                    val = val.substring(1, val.length()-1);
                varMetadataPropertiesMap.put(key, val);
            }

            ITransform transform = _transformGateway.get(varMetadataPropertiesMap.get("output"));
            List<Object> values = _valueGateway.get(varName);

            VariableMetadata varMetadataClass = new VariableMetadata(
                varName,
                values,
                varMetadataPropertiesMap,
                transform);

            allVarMetadata.put(varName, varMetadataClass);
        }

        return allVarMetadata;
    }

    public VariableMetadata getMetadata(String variableId) {
        return _allVarMetadata.get(variableId);
    }
}
