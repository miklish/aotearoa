package com.christoff.aotearoa.test;


import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileHelper;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


public class TestValues
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

    /***
     * Tests that AO can handle replacement text with symbols such as '$' in them.
     * Added: 2.0.20
     */
    @Test
    public void testValuesWithSpecialChars()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values_special_symbols.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");
        exts.add("xml");

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

    /***
     * CONSUL_URL default value (from _metadata.yml) should be inserted in consul.yml
     * E.g.: in consul.yml, should see: consulUrl: "https://consul.com"
     */
    @Test
    public void testMissingValueUseMetadataDefault()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values_missing.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");
        exts.add("xml");

        ValueInjectRequest rq = new ValueInjectRequest();
        rq.metadataLoc = metadataFilename;
        rq.outputDir = outputFolder;
        rq.templateDir = templateFolder;
        rq.configValsLoc = valuesFilename;
        rq.templateFileExtensions = exts;

        boolean isException = false;
        ValueInjectResponse resp = null;
        try {
            resp = new ValueInjectInteractor(rq).exec();
        } catch(ConfigException c) {
            isException = true;
            System.out.println("ERROR: " + c.getMessage());
        }

        // Process response
        Assert.assertTrue(!isException && resp.resultCode.equals(ValueInjectResponse.SUCCESS));
    }

    /***
     * CONSUL_URL is not in _values_missing.yml
     * and
     * CONSUL_URL has no default value in _metadata_nodefault.yml - should generate an error
     */
    @Test
    public void testMissingValueNoMetadataDefault()
    {
        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata_nodefault.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values_missing.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");
        exts.add("xml");

        ValueInjectRequest rq = new ValueInjectRequest();
        rq.metadataLoc = metadataFilename;
        rq.outputDir = outputFolder;
        rq.templateDir = templateFolder;
        rq.configValsLoc = valuesFilename;
        rq.templateFileExtensions = exts;

        boolean isException = false;
        ValueInjectResponse resp = null;
        try {
            resp = new ValueInjectInteractor(rq).exec();
        } catch(ConfigException c) {
            isException = true;
            System.out.println("ERROR: " + c.getMessage());
        }

        // Process response
        Assert.assertTrue(isException);
    }

    /***
     * Test whether the ${ROUTER_INSTANCE} token in a router.yml comment has been replaced with the value
     * 3f96a6b2-3838-11eb-adc1-0242ac120002
     */
    @Test
    public void testTokensInComments()
    {
        // ${ROUTER_INSTANCE} in router.yml comment -- look for ${ROUTER_INSTANCE} replaced with 3f96a6b2-3838-11eb-adc1-0242ac120002

        Path templatFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config");
        String templateFolder = templatFolderPath.toFile().getAbsolutePath();

        Path outputFolderPath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config-out");
        String outputFolder = outputFolderPath.toFile().getAbsolutePath();

        Path metadataFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_metadata.yml");
        String metadataFilename = metadataFilenamePath.toFile().getAbsolutePath();

        Path valuesFilenamePath = Paths.get("src","test","resources", CONFIG_FOLDER, "00.A.shared","_values_unique_val.yml");
        String valuesFilename = valuesFilenamePath.toFile().getAbsolutePath();

        List exts = new LinkedList();
        exts.add("yml");
        exts.add("xml");

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

        Path outputFilePath = Paths.get("src","test","resources", CONFIG_FOLDER, "03.service-config-out", "router.yml");
        String outputFile = outputFilePath.toFile().getAbsolutePath();
        PersistenceFileHelper pfh = new PersistenceFileHelper();
        String templateContents = pfh.getFileInfo(outputFile, false, true, false).string;

        // Process response
        Assert.assertTrue(
            resp.resultCode.equals(ValueInjectResponse.SUCCESS) &&
             !templateContents.contains("3f96a6b2-3838-11eb-adc1-0242ac120002")
        );
    }

}

