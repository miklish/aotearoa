package com.christoff.aotearoa.test;

import com.christoff.aotearoa.extern.gateway.metadata.MetadataTemplateFileGateway;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class TestMetadataLoaders
{
    @Test
    public void testLoadFromTemplatesStandard()
    {
        Path templatFolderPath = Paths.get("src","test","resources", "standard", "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", "standard", "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", "standard", "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        String pattern = "\\$\\{(.*?)\\}";
        List exts = new LinkedList();
        exts.add("yml");

        MetadataTemplateFileGateway metfg = new MetadataTemplateFileGateway(
            pattern,
            templateFolder,
            exts,
            metadataFilename,
            valuesFilename);

        Map mapValues = metfg.collectMetadataFromValues();
        Map templates = metfg.collectMetadataFromTemplates();

        Assert.assertNotNull(templates);
    }

    @Test
    public void testLoadFromTemplatesWithProperties()
    {
        Path templatFolderPath = Paths.get("src","test","resources", "withoptions", "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", "withoptions", "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", "withoptions", "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");

        MetadataTemplateFileGateway metfg = new MetadataTemplateFileGateway(
            null,
            templateFolder,
            exts,
            metadataFilename,
            valuesFilename);

        //Map mapValues = metfg.collectMetadataFromValues();
        Map templates = metfg.collectMetadataFromTemplates();

        Assert.assertNotNull(templates);
    }
}
