package com.christoff.aotearoa.test;


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
    public void testFilenameHandling() {
        String base = System.getProperty("user.dir");
        String fileId = base + "/" + "src/main\\resources/config/_diff.yml";
        String nFileId = normalize(fileId);
        
        System.out.println("Normalized Path: " + nFileId);
        System.out.println("Full Path: " + getFullPath(nFileId));
        System.out.println("Relative Path: " + getPath(nFileId));
        System.out.println("Prefix: " + getPrefix(nFileId));
        System.out.println("Extension: " + getExtension(nFileId));
        System.out.println("Base: " + getBaseName(nFileId));
        System.out.println("Name: " + getName(nFileId));
    
        File file = getFile(normalize(nFileId));
        Assert.assertNotNull(file);
        
        String missingFileId = base + "/" + "src/main\\resources/config/_diff1.yml";
        String nMissingFileId = normalize(missingFileId);
        File file2 = getFile(nMissingFileId);
        Assert.assertFalse(file2.exists());
        
        String dirId = getFullPath(nFileId);
        File dir = getFile(dirId);
        Assert.assertTrue(dir.exists());
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

