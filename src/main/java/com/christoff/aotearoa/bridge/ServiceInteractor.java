package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.intern.gateway.IServiceConfigDataGateway;
import com.christoff.aotearoa.intern.gateway.IServiceValueGateway;
import com.christoff.aotearoa.intern.view.IServicePresenter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

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

    private Map<String, Object> _diffMap = null;
    private ServiceRequest _rq = null;

    public ServiceInteractor(
            IServiceConfigDataGateway configGateway,
            IServiceValueGateway valueGateway,
            IServicePresenter presenter
    ) {
        _configGateway = configGateway;
        _valueGateway = valueGateway;
        _presenter = presenter;
    }

    public ServiceResponse exec(ServiceRequest request)
    {
        _rq = request;

        // Load in the diff file
        _diffMap = _configGateway.get(request.configId);

        // Resolve 'use' values
        Map<String,String> useMap = getUseMap();

        Map<String,Object> variablesMap = (Map<String, Object>) _diffMap.get(VARIABLES);
        Map<String,Object> filesMap = (Map<String, Object>) _diffMap.get(FILES);

        // dispatch sections of the diff file to different methods/classes

        int x = 1;
        return new ServiceResponse("Success", "SUCCESS");
    }

    private Map<String,String> getUseMap()
    {
        Map<String,String> allFileValues = new HashMap<String,String>();

        // get root of 'use' section in diff
        Map<String, Object> useMapInfo = (Map<String, Object>) _diffMap.get(USE);
        Set<String> useFiles = useMapInfo.keySet();

        // loop the files in the 'use' section
        for(String useFileKey : useFiles)
        {
            // Root of the 'lookup location' section for a single file in the 'use' section
            System.out.println("current use-file : " + useFileKey);
            Map<String,Object> useFileVarPointersRoot = (Map<String,Object>) useMapInfo.get(useFileKey);
            System.out.println("use-file: baseId= " + _rq.baseId + " dataId=" + useFileKey);

            // load the source data
            Map<String, Object> useFileSourceDataRoot = _configGateway.get(_rq.baseId, useFileKey);

            // now we pair (a) root of the data-location pointers + (b) root of the source data
            Map<String,String> fileResolutions =
                resolveUseFileVariables(useFileVarPointersRoot, useFileSourceDataRoot);

            allFileValues.putAll(fileResolutions);
        }

        return allFileValues;
    }

    private Map<String,String> resolveUseFileVariables(
        Map<String,Object> varPointersRoot, Map<String,Object> sourceDataRoot)
    {
        // do a depth-first search of varPointersRoot until reach a tag (bottom)

            // check the same locations in sourceDataRoot

            // 

        return new HashMap<String,String>();
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
