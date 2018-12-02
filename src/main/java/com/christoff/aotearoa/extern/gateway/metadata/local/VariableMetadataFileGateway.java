package com.christoff.aotearoa.extern.gateway.metadata.local;

import com.christoff.aotearoa.extern.gateway.YamlHelper;
import com.christoff.aotearoa.intern.gateway.metadata.IVariableMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataFormatException;
import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import com.christoff.aotearoa.intern.gateway.transform.ITransform;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FilenameUtils.normalize;

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
    private YamlHelper _yamlHelper;
    private Map<String, VariableMetadata> _allVarMetadata;

    public VariableMetadataFileGateway(
        String diffFilename,
        IValueGateway valueGateway,
        ITransformGateway transformGateway)
    {
        _diffFilename = diffFilename;
        _valueGateway = valueGateway;
        _transformGateway = transformGateway;
        _yamlHelper = new YamlHelper();
        _allVarMetadata = getAllConfigMetadata();
    }

    private class FileInfo
    {
        String id = null;
        String nId = null;
        File file = null;
        boolean exists;
        boolean isFile = true;
        Map<String, Object> map = null;
    }

    private FileInfo getFileInfo(String configId, boolean buildYaml)
    {
        FileInfo info = new FileInfo();
        info.id = configId;
        info.nId = normalize(configId);
        info.file = getFile(info.nId);
        info.exists = info.file.exists();
        info.isFile = info.file.isFile();

        if(info.exists && buildYaml) {
            try {
                info.map = _yamlHelper.loadYaml(info.file);
            } catch (IOException e) {
                throw new MetadataFormatException(e.getMessage());
            }
        }
        return info;
    }

    // get all config values
    public Map<String,VariableMetadata> getAllConfigMetadata() {
        return _allVarMetadata;
    }

    // get all config values
    private Map<String,VariableMetadata> allConfigMetadata()
    {
        Map<String, Object> metadataMap =
            (Map<String, Object>) getFileInfo(_diffFilename, true).map.get(VARIABLES);

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

    private static String getConfigGroupId(String configId) {
        // get first part
        String[] parts = configId.split("/");

        // ensure at least 2 parts
        if(parts.length < 2)
            return null;

        String format = parts[0].trim().toLowerCase();

        if(format.endsWith(".yml"))
            format = format.substring(0,parts[0].length()-4);

        return format;
    }

    private static String getConfigFileName(String configId)
    {
        // get first part
        String[] parts = configId.split("/");

        // ensure at least 2 parts
        if(parts.length < 2)
            return null;

        String format = parts[0].trim().toLowerCase();

        if(!format.endsWith(".yml"))
            return format + ".yml";
        else
            return format;
    }
}
