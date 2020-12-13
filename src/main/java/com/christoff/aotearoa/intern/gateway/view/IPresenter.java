package com.christoff.aotearoa.intern.gateway.view;

public interface IPresenter
{
    LogLevel TRACE = LogLevel.TRACE;
    LogLevel DEBUG = LogLevel.DEBUG;
    LogLevel WARN = LogLevel.WARN;
    LogLevel INFO = LogLevel.INFO;
    LogLevel QUIET = LogLevel.QUIET;
    
    void tokenDefinedNotUsed(String tagName);
    void tokenDefinedNoValueFound(String tagName);
    void persistingValuesBegin();
    void persistingValuesEnd();
    void emptyKeystore(String keystoreName);
    void templateFileNotFoundOrMalformed(String templateFile);

    void collectingMetadataFromTemplates(String templateFolder);
    void loadingTemplate(String templateName);
    void noTokensFoundInTemplate(String templateName);
    void mergingTokenFromTemplate(String templateName);
    void tokenFound(String token);
    void tokenAlreadyExists(String tokenName);
    void tokenAdded(String tokenName);
    void tokenHasNoProperties();
    void tokenHasProperty(String property);

    void outputLocationLocationDoesNotExist(String outputLocation);
    void outputLocationLocationCreated(String outputLocation);
}
