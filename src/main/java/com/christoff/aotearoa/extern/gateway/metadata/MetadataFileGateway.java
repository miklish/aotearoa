package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.intern.gateway.metadata.IMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataIOException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/***
    This class returns both the variable metadata and the values of variables
    required in config files
 */
public class MetadataFileGateway implements IMetadataGateway
{
    private static final String VARIABLES = "variables";
    private String _metadataFilename;
    private PersistenceFileHelper _fileSysHelper;

    public MetadataFileGateway(String metadataFilename) {
        _metadataFilename = metadataFilename;
        _fileSysHelper = new PersistenceFileHelper();
    }

    @Override
    public Map<String, Metadata> getMetadata()
    {
        // return empty map if no metadata file chosen by user
        if(_metadataFilename == null)
            return new HashMap<>();

        return build(loadYaml());
    }

    // initialize all config values
    public Map<String, Object> loadYaml()
        throws MetadataException, MetadataIOException
    {
        // read in the entire metadata file
        Map<String, Object> allConfigDataMap =
            _fileSysHelper.getFileInfo(_metadataFilename, true, false).map;

        // check that it contains data
        if(allConfigDataMap == null || allConfigDataMap.size() == 0)
            return new HashMap<>();
        else
            return (Map<String, Object>) allConfigDataMap.get(VARIABLES);
    }

    // build Metadata classes
    private Map<String, Metadata> build(Map<String, Object> variablesMetadataMapObjects)
    {
        // check there are some variable metadata instances to convert
        if(variablesMetadataMapObjects.size() == 0)
            throw new MetadataException("No variable metadata found");

        // build Metadata map
        Map<String, Metadata> variablesMetadataMap = new HashMap<>();

        for(Map.Entry<String,Object> varMetadataEntry : variablesMetadataMapObjects.entrySet())
        {
            // get the key : the variable's name
            String varName = varMetadataEntry.getKey();

            // convert the value, and Object, from its native type (Map<String,Object>)
            // to Map<String,String> to ensure consistent single value-type for processing
            Map<String, List<String>> varMetadataPropertiesMap = new HashMap<>();

            for(Map.Entry<String,Object> e : ((Map<String,Object>) varMetadataEntry.getValue()).entrySet())
            {
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

                    for (Object o : valueList)
                    {
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

            Metadata varMetadataClass = new Metadata(varName, varMetadataPropertiesMap);
            variablesMetadataMap.put(varName, varMetadataClass);
        }

        return variablesMetadataMap;
    }
}
