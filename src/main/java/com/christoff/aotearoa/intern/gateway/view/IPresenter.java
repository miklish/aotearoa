package com.christoff.aotearoa.intern.gateway.view;

public interface IPresenter
{
    void tagDefinedNotUsed(String tagName);
    void tagDefinedNoValueFound(String tagName);
    void persistingValuesBegin();
    void persistingValuesEnd();
    void emptyKeystore(String keystoreName);
    void templateFileNotFoundOrMalformed(String templateFile);
}
