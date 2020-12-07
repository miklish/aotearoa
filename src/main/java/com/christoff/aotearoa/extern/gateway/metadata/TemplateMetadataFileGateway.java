package com.christoff.aotearoa.extern.gateway.metadata;

import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import com.christoff.aotearoa.extern.gateway.persistence.TemplateRegexResolver;
import com.christoff.aotearoa.intern.gateway.metadata.*;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateIOException;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TemplateMetadataFileGateway implements ITemplateMetadataGateway
{
    private static final boolean IS_RECURSIVE = false;

    private IPresenter _presenter;
    private TemplateRegexResolver _regexResolver;
    private String _templateFolder;
    private String[] _templateExtensions;
    private PersistenceFileHelper _fileSysHelper;

    public TemplateMetadataFileGateway(
        IPresenter presenter,
        TemplateRegexResolver regexResolver,
        String templateFolder,
        List<String> templateExtensions
    ) {
        _presenter = presenter;
        _regexResolver = regexResolver;
        _templateFolder = templateFolder;
        _templateExtensions = new String[templateExtensions.size()];
        _templateExtensions = templateExtensions.toArray(_templateExtensions);

        _fileSysHelper = new PersistenceFileHelper();
    }

    /***
     * templateName -> Map[ tokenName, List[Metadata] ]
     *
     * @return
     * @throws TemplateIOException
     * @throws MetadataIOException
     */
    public Map<String, Map<String, List<Metadata>>> getMetadata()
        throws TemplateIOException, MetadataIOException
    {
        _presenter.collectingMetadataFromTemplates(_templateFolder);

        Map<String, Map<String, List<Metadata>>> templateMetadataMap = new HashMap<>();

        // get template files
        File templateFolderFile = new File(_templateFolder);
        List<File> files = (List<File>) FileUtils.listFiles(templateFolderFile, _templateExtensions, IS_RECURSIVE);

        // loop through each template file
        for (File file : files)
        {
            // read the template files as Strings
            String templateName = file.getName();
            String templateContents;

            _presenter.loadingTemplate(templateName);

            // read in the template file
            templateContents = _fileSysHelper.getFileInfo(file, false, true).string;

            // get the map of metadata objects from the template
            Map<String, List<Metadata>> newMetadataObjects = _regexResolver.extractTemplateMetadata(templateContents);
            templateMetadataMap.put(templateName, newMetadataObjects);
        }

        return templateMetadataMap;
    }
}
