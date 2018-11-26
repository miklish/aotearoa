package com.christoff.aotearoa.test;


import com.christoff.aotearoa.extern.gateway.ServiceConfigFileGateway;
import com.christoff.aotearoa.intern.gateway.ConfigDataException;
import com.christoff.aotearoa.intern.gateway.IServiceConfigDataGateway;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FilenameUtils.*;


public class TestFiles {
    
    @Test
    public void testFilenameHandling()
    {
        String userdir = System.getProperty("user.dir");
        String base = userdir + "/src/main/resources/config/";
        String outBase = userdir + "/src/main/resources/config-out/";
        String baseFake = userdir + "/src/main/resources/config-fake/";
        String newBase = userdir + "/src/main/resources/config3/";
        String diff = base + "_diff.yml";
        String diffVals = "_diff-values.yml";
        String diffFake = base + "_diff_fake.yml";
    
        //IServiceConfigDataGateway g = new ServiceConfigFileGateway();
        ServiceConfigFileGateway g = new ServiceConfigFileGateway();
        
        // test get
        Map<String, Object> diffMap = g.get(diff);
        Assert.assertTrue(diffMap.size() == 3);
        Assert.assertTrue(
            diffMap.containsKey("use") &&
            diffMap.containsKey("variables") &&
            diffMap.containsKey("files")
        );
        Assert.assertTrue(
            diffMap.get("use") instanceof Map &&
                diffMap.get("variables") instanceof Map &&
                diffMap.get("files") instanceof Map
        );
        
        // check if file exists
        Assert.assertTrue(g.configExists(diff));
        Assert.assertFalse(g.configExists(diffFake));
        // pass in directory as config
        Assert.assertFalse(g.configExists(base));
    
        // check if base exists
        Assert.assertTrue(g.baseExists(base));
        Assert.assertFalse(g.baseExists(baseFake));
        // pass in config as config
        Assert.assertFalse(g.baseExists(diff));
        
        // update diff into output dir
        Map<String, Object> updateMap = g.get(diff);
        String outputConfig = g.getConfigBase(outBase) + g.getConfigName(diff);
        g.save(updateMap, outputConfig, true);
        
        // Test with directory
        boolean b = false;
        try {
            b = g.save(updateMap, g.getConfigBase(outBase), g.getConfigName(outBase), true);
        } catch(ConfigDataException e) {
            b = true;
        }
        Assert.assertTrue(b);
        
        // Test with config
        b = false;
        try {
            b = g.save(updateMap, g.getConfigBase(diff), g.getConfigName(diff), true);
        } catch(ConfigDataException e) {
            b = false;
        }
        Assert.assertTrue(b);
        
        /*
        public Map<String, Object> get(String configId);
        public void save(Map<String, Object> map, String baseId, String configName, boolean deleteIfExists);
        public void save(Map<String, Object> map, String configId, boolean deleteIfExists);
        public boolean configExists(String configId);
        public boolean configExists(String baseId, String configName);
        public boolean baseExists(String baseId);
        public boolean createBase(String baseId);
        public boolean deleteBase(String baseId, boolean deleteIfNonEmpty);
        */
    }
    
/*
    @Test
    public void testInjectedBean() {
        InjectedBean injectedBean = SingletonServiceFactory.getBean(InjectedBean.class);
        Assert.assertEquals("Injected Bean", injectedBean.name());
    }

    @Test
    public void testGetSingleBean() {
        A a = SingletonServiceFactory.getBean(A.class);
        Assert.assertEquals("a real", a.a());

        B b = SingletonServiceFactory.getBean(B.class);
        Assert.assertEquals("b test", b.b());

        C c = SingletonServiceFactory.getBean(C.class);
        Assert.assertEquals("a realb test", c.c());
    }

    @Test
    public void testGetMultipleBean() {
        Processor[] processors = SingletonServiceFactory.getBeans(Processor.class);
        Assert.assertEquals(processors.length, 3);
        Arrays.stream(processors).forEach(processor -> System.out.println(processor.process()));
    }

    @Test
    public void testMultipleInterfaceOneBean() {
        D1 d1 = SingletonServiceFactory.getBean(D1.class);
        D2 d2 = SingletonServiceFactory.getBean(D2.class);
        Assert.assertEquals(d1, d2);
    }

    @Test
    public void testMultipleToMultiple() {
        E[] e = SingletonServiceFactory.getBeans(E.class);
        Arrays.stream(e).forEach(o -> System.out.println(o.e()));
        F[] f = SingletonServiceFactory.getBeans(F.class);
        Arrays.stream(f).forEach(o -> System.out.println(o.f()));
    }

    @Test
    public void testSingleWithProperties() {
        G g = SingletonServiceFactory.getBean(G.class);
        Assert.assertEquals("Sky Walker", g.getName());
        Assert.assertEquals(23, g.getAge());

    }

    //@Test
    public void testMultipleWithProperties() {
        J[] j = SingletonServiceFactory.getBeans(J.class);
        Arrays.stream(j).forEach(o -> System.out.println(o.getJack()));
        K[] k = SingletonServiceFactory.getBeans(K.class);
        Arrays.stream(k).forEach(o -> System.out.println(o.getKing()));

    }

    @Test
    public void testMap() {
        LImpl l = (LImpl)SingletonServiceFactory.getBean(L.class);
        Assert.assertEquals("https", l.getProtocol());
        Assert.assertEquals(8080, l.getPort());
        Assert.assertEquals(2, l.getParameters().size());
    }

    @Test
    public void testConstructorWithParameters() {
        MImpl m = (MImpl)SingletonServiceFactory.getBean(M.class);
        Assert.assertEquals(5, m.getValue());
    }

    @Test
    public void testInfo() {
        Info info = SingletonServiceFactory.getBean(Info.class);
        Assert.assertEquals("contact", info.getContact().getName());
        Assert.assertEquals("license", info.getLicense().getName());
    }

    @Test
    public void testInfoValidator() {
        Validator<Info> infoValidator = SingletonServiceFactory.getBean(Validator.class, Info.class);
        Info info = SingletonServiceFactory.getBean(Info.class);
        Assert.assertTrue(infoValidator.validate(info));
    }

    @Test
    public void testArrayFromSingle() {
        // get an array with only one implementation.
        A[] a = SingletonServiceFactory.getBeans(A.class);
        Assert.assertEquals(1, a.length);
    }

    @Test
    public void testSingleFromArray() {
        // get the first object from an array of impelementation in service.yml
        E e = SingletonServiceFactory.getBean(E.class);
        Assert.assertEquals("e1", e.e());
    }

    @Test
    public void testArrayNotDefined() {
        Dummy[] dummies = SingletonServiceFactory.getBeans(Dummy.class);
        Assert.assertNull(dummies);
    }

    @Test
    public void testObjectNotDefined() {
        Dummy dummy = SingletonServiceFactory.getBean(Dummy.class);
        Assert.assertNull(dummy);
    }

    @Test
    public void testInitializerInterfaceWithBuilder() {
        ChannelMapping channelMapping = SingletonServiceFactory.getBean(ChannelMapping.class);
        Assert.assertNotNull(channelMapping);
        Assert.assertTrue(channelMapping.transform("ReplyTo").startsWith("aggregate-destination-"));
    }
 */
}

