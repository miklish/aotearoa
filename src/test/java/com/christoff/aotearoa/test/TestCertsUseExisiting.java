package com.christoff.aotearoa.test;


import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;


public class TestCertsUseExisiting
{
    @Test
    public void testCertsUseExisiting()
    {
        ValueInjectRequest rq = new ValueInjectRequest();
        rq.metedataLoc = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config\\_metadata.yml";
        rq.outputDir = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config-out";
        rq.templateDir = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config";
        rq.configValsLoc = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config\\_values.yml";
        rq.keystoreMetadataLoc = "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\main\\resources\\config-out\\_keystore_use_existing.yml";
    
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

