package com.christoff.aotearoa.test;


import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import com.christoff.aotearoa.intern.gateway.view.LogLevel;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


public class TestCertsUseExisiting
{
    static final String CONFIG_FOLDER = "legacy";
    static final String LOG_LEVEL = LogLevel.TRACE.levelId();

    @Test
    public void testCertsCreateNew()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        Path keystoreFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs","_keystore_use_existing.yml");
        String keystoreFolder = keystoreFolderPath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");

        // --

        ValueInjectRequest rq = new ValueInjectRequest();
        rq.metadataLoc = metadataFilename;
        rq.outputDir = outputFolder;
        rq.templateDir = templateFolder;
        rq.configValsLoc = valuesFilename;
        rq.keystoreMetadataLoc = keystoreFolder;
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
