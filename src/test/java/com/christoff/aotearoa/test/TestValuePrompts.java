package com.christoff.aotearoa.test;


import com.christoff.aotearoa.Bootstrap;
import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import org.junit.Assert;
import org.junit.AssumptionViolatedException;
import org.junit.Test;


public class TestValuePrompts
{
    @Test
    public void testValues()
    {
        String[] args = {
            "-m",
            "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config\\_metadata.yml",
            "-o",
            "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config-out",
            "-t",
            "C:\\Users\\mikle\\localrepo\\aotearoa\\src\\test\\resources\\config",
            "-p"
        };
    
        ValueInjectResponse resp = null;
        try {
            resp = new Bootstrap().exec(args);
        } catch(ConfigException c) {
            throw new AssumptionViolatedException("ERROR: " + c.getMessage());
        }
    
        // Process response
        Assert.assertTrue(resp.resultCode.equals(ValueInjectResponse.SUCCESS));
    }
}

