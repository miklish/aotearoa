package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.intern.gateway.metadata.IMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
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
    
    // initialize all config values
    public Map<String, Object> getMetadataMap()
        throws MetadataException
    {
        // read in the entire metadata file
        Map<String, Object> allConfigDataMap =
            _fileSysHelper.getFileInfo(_metadataFilename, true, false).map;

        // check that it contains data
        if(allConfigDataMap == null || allConfigDataMap.size() == 0)
            throw new MetadataException("No data found in " + _metadataFilename);

        return (Map<String, Object>) allConfigDataMap.get(VARIABLES);
    }
}
