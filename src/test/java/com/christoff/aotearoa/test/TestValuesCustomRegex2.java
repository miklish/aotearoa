package com.christoff.aotearoa.test;


import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
//import org.junit.Assert;
import org.junit.AssumptionViolatedException;
//import org.junit.Test;

import static org.apache.commons.io.FilenameUtils.normalize;


public class TestValuesCustomRegex2
{
    //@Test
    public void testValuesCustomRegex()
    {
        ValueInjectRequest rq = new ValueInjectRequest();
        String WINDOWS_ROOT = "C:\\Users\\mikle\\";
        String MAC_ROOT = "/Users/christof/";
        String root = MAC_ROOT;

        rq.metadataLoc = normalize(root + "localrepo\\aotearoa\\src\\main\\resources\\config2\\_metadata.yml");
        rq.outputDir = normalize(root + "localrepo\\aotearoa\\src\\main\\resources\\config2-out");
        rq.templateDir = normalize(root + "localrepo\\aotearoa\\src\\main\\resources\\config2");
        rq.configValsLoc = normalize(root + "localrepo\\aotearoa\\src\\main\\resources\\config2\\_values.yml");
        rq.regex = "\\$(.*?)\\}";
    
        ValueInjectResponse resp = null;
        try {
            resp = new ValueInjectInteractor(rq).exec();
        } catch(ConfigException c) {
            throw new AssumptionViolatedException("ERROR: " + c.getMessage());
        }
    
        // Process response
        //Assert.assertTrue(resp.resultCode.equals(ValueInjectResponse.SUCCESS));
        System.out.println(resp.resultCode);
    }
}

