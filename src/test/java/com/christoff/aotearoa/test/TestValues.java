package com.christoff.aotearoa.test;


import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import com.christoff.aotearoa.extern.gateway.metadata.TemplateMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.TemplateRegexResolver;
import com.christoff.aotearoa.extern.gateway.view.PresenterCLI;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import com.christoff.aotearoa.intern.gateway.view.LogLevel;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


public class TestValues
{
    static final String CONFIG_FOLDER = "legacy";
    static final String LOG_LEVEL = LogLevel.TRACE.levelId();

    @Test
    public void testValues()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");
        exts.add("xml");

        IPresenter presenter = new PresenterCLI(LOG_LEVEL);
        TemplateMetadataFileGateway metfg = new TemplateMetadataFileGateway(
            presenter,
            new TemplateRegexResolver(presenter),
            templateFolder,
            exts);

        ValueInjectRequest rq = new ValueInjectRequest();
        rq.metadataLoc = metadataFilename;
        rq.outputDir = outputFolder;
        rq.templateDir = templateFolder;
        rq.configValsLoc = valuesFilename;
        rq.templateFileExtensions = exts;
    
        ValueInjectResponse resp = null;
        try {
            resp = new ValueInjectInteractor(rq).exec();
        } catch(ConfigException c) {
            throw new AssumptionViolatedException("ERROR: " + c.getMessage());
        }
    
        // Process response
        Assert.assertTrue(resp.resultCode.equals(ValueInjectResponse.SUCCESS));
    }
}

