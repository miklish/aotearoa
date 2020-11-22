package com.christoff.aotearoa.bridge;

import java.util.List;

public class ValueInjectRequest
{
    public String metadataLoc;
    public String keystoreMetadataLoc;
    public String templateDir;
    public List<String> templateFileExtensions;
    public String configValsLoc;
    public String outputDir;
    public String serverUrl;
    public String regex;
    public boolean showHelp;
    public boolean usingPrompts;
    public boolean usingEnvVars;
    public String symmetricKey;
    public String logLevelValue;
    public boolean logLevelExists;
}
