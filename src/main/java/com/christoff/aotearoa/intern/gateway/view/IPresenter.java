package com.christoff.aotearoa.intern.gateway.view;

public interface IPresenter
{
    LogLevel DEBUG = LogLevel.DEBUG;
    LogLevel WARN = LogLevel.WARN;
    LogLevel INFO = LogLevel.INFO;
    LogLevel QUIET = LogLevel.QUIET;
    
    void tagDefinedNotUsed(String tagName);
    void tagDefinedNoValueFound(String tagName);
    void persistingValuesBegin();
    void persistingValuesEnd();
    void emptyKeystore(String keystoreName);
    void templateFileNotFoundOrMalformed(String templateFile);
}
