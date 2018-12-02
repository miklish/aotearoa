package com.christoff.aotearoa.test;


import org.junit.Test;

import static org.apache.commons.io.FileUtils.getFile;


public class TestFiles {
    
    @Test
    public void testFilenameHandling()
    {
        /*
        String userdir = System.getProperty("user.dir");
        String base = userdir + "/src/main/resources/config/";
        String outBase = userdir + "/src/main/resources/config-out/";
        String baseFake = userdir + "/src/main/resources/config-fake/";
        String newBase = userdir + "/src/main/resources/config3/";
        String diff = base + "_diff.yml";
        String diffVals = base + "_diff-values.yml";
        String diffFake = base + "_diff_fake.yml";
    
        //IMetadataGatewayOld g = new MetadataFileGatewayOld();
        MetadataFileGatewayOld g = new MetadataFileGatewayOld();
        
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
        // pass in config when method expects base
        Assert.assertFalse(g.baseExists(diff));
        
        // update diff into output dir
        Map<String, Object> updateMap = g.get(diff);
        String outputConfig = g.getConfigBase(outBase) + g.getConfigName(diff);
        g.save(updateMap, outputConfig, true);

        // update diff-values with new value and save into output directory
        //
        // update diff-values into output dir
        Map<String, Object> updateMap2 = g.get(diffVals);
        List<String> tabPasswordList = (List<String>) updateMap2.get("tableau-password");
        String tableauPassword = tabPasswordList.get(0);
        tabPasswordList.set(0, "newPassword");
        String outputConfig2 = g.getConfigBase(outBase) + g.getConfigName(diffVals);
        g.save(updateMap2, outputConfig2, true);
        
        // Test with directory
        boolean b = false;
        try {
            // should return false
            b = g.save(updateMap, g.getConfigBase(outBase), g.getConfigName(outBase), true);
        } catch(MetadataFormatException e) {
            b = true;
        }
        Assert.assertTrue(b);
        
        // Test with config
        b = false;
        try {
            // should return true
            b = g.save(updateMap, g.getConfigBase(diff), g.getConfigName(diff), true);
        } catch(MetadataFormatException e) {
        }
        Assert.assertTrue(b);


        // test pattern matcher
        List<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("\\(\\{.*?}\\)");
        Matcher regexMatcher = regex.matcher("Hello This is {Java} Not {.NET}");
        while (regexMatcher.find()) {//Finds Matching Pattern in String
            matchList.add(regexMatcher.group(1));//Fetching Group from String
        }
        for(String str:matchList) {
            System.out.println(str);
        }
        */
        
        /*
        public Map<String, Object> get(String metadataDir);
        public void save(Map<String, Object> map, String templateDir, String configName, boolean deleteIfExists);
        public void save(Map<String, Object> map, String metadataDir, boolean deleteIfExists);
        public boolean configExists(String metadataDir);
        public boolean configExists(String templateDir, String configName);
        public boolean baseExists(String templateDir);
        public boolean createBase(String templateDir);
        public boolean deleteBase(String templateDir, boolean deleteIfNonEmpty);
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

