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
    public void tokenDefinedNotUsed(String tokenName) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println("WARN: Token " + tokenName + " defined, but never used");
    }
    
    @Override
    public void tokenDefinedNoValueFound(String tokenName) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println("WARN: No Value found for Metadata token " + tokenName);
    }
    
    @Override
    public void persistingValuesBegin() {
        if(_logLevel.level() <= LogLevel.INFO.level())
            System.out.println("INFO: Resolving templates...");
    }
    
    @Override
    public void persistingValuesEnd() {
        if(_logLevel.level() <= LogLevel.INFO.level())
            System.out.println("INFO: Resolving templates... done");
    }
    
    @Override
    public void emptyKeystore(String keystoreName) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println("WARN: Keystore " + keystoreName + " has no certificates");
    }

    @Override
    public void templateFileNotFoundOrMalformed(String templateFile) {
        if(_logLevel.level() <= LogLevel.WARN.level())
            System.out.println(
                "WARN: Template file " +
                templateFile +
                " is specified in the Metadata yaml, but it cannot be found or is incorrectly formatted");
    }

    @Override
    public void collectingMetadataFromTemplates(String templateFolder) {
        if(_logLevel.level() <= LogLevel.INFO.level())
            System.out.println("INFO: Collecting metadata from template files in folder " + templateFolder);
    }

    @Override
    public void loadingTemplate(String templateName) {
        if(_logLevel.level() <= LogLevel.INFO.level())
            System.out.println("INFO: Loading template file " + templateName);
    }

    @Override
    public void noTokensFoundInTemplate(String templateName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("TRACE: No tokens found in template file " + templateName);
    }

    @Override
    public void mergingTokenFromTemplate(String templateName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("TRACE: Merging tokens from template file " + templateName);
    }

    @Override
    public void tokenFound(String token) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("TRACE: Found token " + token);
    }

    @Override
    public void tokenAlreadyExists(String tokenName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("TRACE: Token " + tokenName + " already exists in map");
    }

    @Override
    public void tokenAdded(String tokenName) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("TRACE: New token " + tokenName + " added to map");
    }

    @Override
    public void tokenHasNoProperties() {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("TRACE: Token has no properties");
    }

    @Override
    public void tokenHasProperty(String property) {
        if(_logLevel.level() <= LogLevel.TRACE.level())
            System.out.println("TRACE: Token has property " + property);
    }

    @Override
    public void outputLocationLocationDoesNotExist(String outputLocation) {
        if(_logLevel.level() <= LogLevel.INFO.level())
            System.out.println("INFO: The specified output folder " + outputLocation + " does not exist. Creating...");
    }

    @Override
    public void outputLocationLocationCreated(String outputLocation) {
        if(_logLevel.level() <= LogLevel.INFO.level())
            System.out.println("INFO: ...the output folder " + outputLocation + " was successfully created");
    }
}
