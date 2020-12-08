package com.christoff.aotearoa.test;


import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


public class TestKeystoreBuild
{
    static final String CONFIG_FOLDER = "legacy";

    @BeforeClass
    public static void beforeAllTestMethods() {
        File secretsOut = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs-out").toFile();
        if(!secretsOut.exists()) secretsOut.mkdir();

        File serviceOut = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config-out").toFile();
        if(!serviceOut.exists()) serviceOut.mkdir();
    }

    @Test
    public void testKeystoreBuild()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        Path keystoreFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs","_keystore_create_new.yml");
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

    /***
     * Test having keystore in folder different than the template folder
     */
    @Test
    public void testKeystoreBuildDiffFolder()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        Path keystoreFolderPath = Paths.get("src","test","resources", "nometa", "02.secrets-certs","_keystore_unique_name.yml");
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

    /***
     * Test having keystore (_keystore.yml) in folder different than the template folder
     * This should generate an error since there is another file named _keystore.yml in the template folder
     * -- this filename conflict should generate an error
     */
    @Test
    public void testKeystoreBuildDiffFolderConflictName()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "02.secrets-certs-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        Path keystoreFolderPath = Paths.get("src","test","resources", "nometa", "02.secrets-certs","_keystore.yml");
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
        boolean configException = false;
        try {
            resp = new ValueInjectInteractor(rq).exec();
        } catch(ConfigException c) {
            configException = true;
        }

        // Process response
        Assert.assertTrue(configException);
    }
}


