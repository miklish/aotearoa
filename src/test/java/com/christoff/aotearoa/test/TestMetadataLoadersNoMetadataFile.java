package com.christoff.aotearoa.test;

import com.christoff.aotearoa.extern.gateway.metadata.MetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.TemplateMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.ValueMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.TemplateRegexResolver;
import com.christoff.aotearoa.extern.gateway.view.PresenterCLI;
import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataMerge;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import com.christoff.aotearoa.intern.gateway.view.LogLevel;
import org.junit.Assert;
import org.junit.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class TestMetadataLoadersNoMetadataFile
{
    static final String CONFIG_FOLDER = "nometa";
    static final String LOG_LEVEL = LogLevel.TRACE.levelId();

    /***
     * Uses legacy config files, with _metadata.yml
     */
    @Test
    public void testCombineAllMetadata()
    {
        IPresenter presenter = new PresenterCLI(LOG_LEVEL);

        Map<String, Metadata> allVarMetadata =
            (new MetadataMerge(presenter)).merge(
                loadTemplateMetadata(CONFIG_FOLDER, LOG_LEVEL),
                loadMetadata(CONFIG_FOLDER, LOG_LEVEL),
                loadValueMetadata(CONFIG_FOLDER, LOG_LEVEL));

        Assert.assertNotNull(allVarMetadata);
    }

    @Test
    public void testLoadTemplateMetadata()
    {
        Map<String,Map<String,List<Metadata>>> tm = loadTemplateMetadata(CONFIG_FOLDER, LOG_LEVEL);
        Assert.assertNotNull(tm);
    }

    @Test
    public void testLoadMergedTemplateMetadata()
    {
        Map<String,Metadata> tmm = loadMergedTemplateMetadata(CONFIG_FOLDER, LOG_LEVEL);
        Assert.assertNotNull(tmm);
    }

//    @Test
//    public void testLoadMetadata()
//    {
//        Map<String,Metadata> m = loadMetadata(CONFIG_FOLDER, LOG_LEVEL);
//        Assert.assertNotNull(m);
//    }

    @Test
    public void testLoadValueMetadata()
    {
        Map<String,Metadata> vm = loadValueMetadata(CONFIG_FOLDER, LOG_LEVEL);
        Assert.assertNotNull(vm);
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

        IPresenter presenter = new PresenterCLI(loglevel);
        TemplateMetadataFileGateway metfg = new TemplateMetadataFileGateway(
            presenter,
            new TemplateRegexResolver(presenter),
            templateFolder,
            exts);

        return metfg.getMetadata();
    }

    public Map<String,Metadata> loadMergedTemplateMetadata(String configFolder, String loglevel)
    {
        Map<String,Map<String,List<Metadata>>> unmergedTemplateMetadata = loadTemplateMetadata(configFolder, loglevel);
        MetadataMerge tmm = new MetadataMerge(new PresenterCLI(loglevel));
        return tmm.mergeTemplateMetadata(unmergedTemplateMetadata);
    }

    /***
     * null Metadata
     *
     * @param configFolder
     * @param loglevel
     * @return
     */
    public Map<String,Metadata> loadMetadata(String configFolder, String loglevel)
    {
        // Path metadataFilenamePath = Paths.get("src","test","resources", configFolder, "00.A.shared","_metadata.yml");
        // String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        MetadataFileGateway mfg = new MetadataFileGateway(null);
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
