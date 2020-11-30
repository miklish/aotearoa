package com.christoff.aotearoa.test;

import com.christoff.aotearoa.extern.gateway.metadata.MetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.TemplateMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.ValueMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.view.PresenterCLI;
import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataMerge;
import com.christoff.aotearoa.intern.gateway.view.LogLevel;
import org.junit.Assert;
import org.junit.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class TestMetadataLoaders
{
    @Test
    public void testLoadTemplateMetadataStandard()
    {
        Map<String,Map<String,List<Metadata>>> tm = loadTemplateMetadata("standard", LogLevel.TRACE.levelId());
        Assert.assertNotNull(tm);
    }

    @Test
    public void testLoadMergedTemplateMetadataStandard()
    {
        Map<String,Metadata> tmm = loadMergedTemplateMetadata("standard", LogLevel.TRACE.levelId());
        Assert.assertNotNull(tmm);
    }

    @Test
    public void testLoadTemplateMetadataWithProperties()
    {
        Map<String,Map<String,List<Metadata>>> tm = loadTemplateMetadata("withoptions", LogLevel.TRACE.levelId());
        Assert.assertNotNull(tm);
    }

    @Test
    public void testLoadMergedTemplateMetadataWithProperties()
    {
        Map<String,Metadata> tmm = loadMergedTemplateMetadata("withoptions", LogLevel.TRACE.levelId());
        Assert.assertNotNull(tmm);
    }

    @Test
    public void testLoadMetadataStandard()
    {
        Map<String,Metadata> m = loadMetadata("standard", LogLevel.TRACE.levelId());
        Assert.assertNotNull(m);
    }

    @Test
    public void testLoadMetadataWithOptions()
    {
        Map<String,Metadata> m = loadMetadata("withoptions", LogLevel.TRACE.levelId());
        Assert.assertNotNull(m);
    }


    // -----------------------------------------------------------------------------------------------------------------


    public Map<String,Map<String,List<Metadata>>> loadTemplateMetadata(String configFolder, String loglevel)
    {
        Path templatFolderPath = Paths.get("src","test","resources", configFolder, "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", configFolder, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", configFolder, "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");

        TemplateMetadataFileGateway metfg = new TemplateMetadataFileGateway(
            new PresenterCLI(loglevel),
            null,
            templateFolder,
            exts);

        return metfg.getMetadataFromTemplates();
    }

    public Map<String,Metadata> loadMergedTemplateMetadata(String configFolder, String loglevel)
    {
        Map<String,Map<String,List<Metadata>>> unmergedTemplateMetadata = loadTemplateMetadata(configFolder, loglevel);
        MetadataMerge tmm = new MetadataMerge(new PresenterCLI(loglevel));
        return tmm.mergeTemplateMetadata(unmergedTemplateMetadata);
    }

    public Map<String,Metadata> loadMetadata(String configFolder, String loglevel)
    {
        Path metadataFilenamePath = Paths.get("src","test","resources", configFolder, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        MetadataFileGateway mfg = new MetadataFileGateway(metadataFilename);
        return mfg.getMetadata();
    }

    public Map<String,Metadata> loadValueMetadata(String configFolder, String loglevel)
    {
        Path valuesFilenamePath = Paths.get("src","test","resources", configFolder, "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        ValueMetadataFileGateway vmg = new ValueMetadataFileGateway(
            new PresenterCLI(loglevel),
            valuesFilename
        );

        return vmg.getMetadata();
    }
}
