package com.christoff.aotearoa.extern.gateway.view;

import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import com.christoff.aotearoa.intern.gateway.view.LogLevel;

public class PresenterCLI implements IPresenter
{
    private final LogLevel _logLevel;
    
    public PresenterCLI(String logLevel) { _logLevel = toLogLevel(logLevel); }
    
    private String clean(String s) { return s == null ? LogLevel.DEBUG.levelId() : s.trim().toLowerCase().substring(0, 1); }
    
    private LogLevel toLogLevel(String logLevelValue) {
        String logLevelId = clean(logLevelValue);
        if(logLevelId.equals(LogLevel.TRACE.levelId())) return LogLevel.TRACE;
        if(logLevelId.equals(LogLevel.DEBUG.levelId())) return LogLevel.DEBUG;
        if(logLevelId.equals(LogLevel.WARN.levelId())) return LogLevel.WARN;
        if(logLevelId.equals(LogLevel.INFO.levelId())) return LogLevel.INFO;
        if(logLevelId.equals(LogLevel.QUIET.levelId())) return LogLevel.QUIET;
        
        throw new ConfigException("Log Level " + logLevelValue + " is not a valid logging level");
    }
    
    @Override
    public void tagDefinedNotUsed(String tagName) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println("WARNING: Tag " + tagName + " defined, but never used");
    }
    
    @Override
    public void tagDefinedNoValueFound(String tagName) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println("WARNING: No Value found for Metadata tag " + tagName);
    }
    
    @Override
    public void persistingValuesBegin() {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.print("Resolving templates...");
    }
    
    @Override
    public void persistingValuesEnd() {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println("done.");
    }
    
    @Override
    public void emptyKeystore(String keystoreName) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println("WARNING: Keystore " + keystoreName + " has no certificates");
    }

    @Override
    public void templateFileNotFoundOrMalformed(String templateFile) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println(
                "WARNING: Template file " +
                templateFile +
                " is specified in the Metadata yaml, but it cannot be found or is incorrectly formatted");
    }

    @Override
    public void collectingMetadataFromTemplates(String templateFolder) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("Collecting metadata from template files in folder " + templateFolder);
    }

    @Override
    public void loadingTemplate(String templateName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("Loading template file " + templateName);
    }

    @Override
    public void noTokensFoundInTemplate(String templateName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("No tokens found in template file " + templateName);
    }

    @Override
    public void mergingTokenFromTemplate(String templateName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("Merging tokens from template file " + templateName);
    }

    @Override
    public void tokenFound(String token) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("Found token " + token);
    }

    @Override
    public void tokenAlreadyExists(String tokenName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("Token " + tokenName + " already exists in map");
    }

    @Override
    public void tokenAdded(String tokenName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("New token " + tokenName + " added to map");
    }

    @Override
    public void tokenHasNoProperties() {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("Token has no properties");
    }

    @Override
    public void tokenHasProperty(String property) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("Token has property " + property);
    }
}
