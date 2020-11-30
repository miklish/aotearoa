package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.intern.gateway.metadata.*;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class ValueMetadataFileGateway implements IValueMetadataGateway
{
    private static final String VARIABLES = "variables";

    private IPresenter _presenter;
    private String _valuesFilename;
    private PersistenceFileHelper _fileSysHelper;

    public ValueMetadataFileGateway(IPresenter presenter, String valuesFilename)
    {
        _presenter = presenter;
        _fileSysHelper = new PersistenceFileHelper();
        _valuesFilename = valuesFilename;
    }

    @Override
    public Map<String, Metadata> getMetadata()
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
}
