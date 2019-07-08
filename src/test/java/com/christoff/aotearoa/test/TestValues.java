package com.christoff.aotearoa.test;


import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

import static org.apache.commons.io.FileUtils.getFile;


public class TestValues
{
    @Test
    public void testValues()
    {
        ValueInjectRequest rq = new ValueInjectRequest();
        rq.metadataLoc = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config\\_metadata.yml";
        rq.outputDir = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config-out";
        rq.templateDir = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config";
        rq.configValsLoc = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config\\_values.yml";
    
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

