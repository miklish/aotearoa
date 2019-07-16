package com.christoff.aotearoa.extern.gateway.view;

import com.christoff.aotearoa.intern.gateway.view.IPresenter;

public class PresenterCLI implements IPresenter
{
    @Override
    public void tagDefinedNotUsed(String tagName) {
        System.out.println("WARNING: Tag " + tagName + " defined, but never used");
    }
    
    @Override
    public void tagDefinedNoValueFound(String tagName) {
        System.out.println("WARNING: No Value found for Metadata tag " + tagName);
    }
    
    @Override
    public void persistingValuesBegin() {
        System.out.print("Resolving templates...");
    }
    
    @Override
    public void persistingValuesEnd() {
        System.out.println("done.");
    }
    
    @Override
    public void emptyKeystore(String keystoreName) {
        System.out.println("WARNING: Keystore " + keystoreName + " has no certificates");
    }

    @Override
    public void templateFileNotFoundOrMalformed(String templateFile) {
        System.out.println(
            "WARNING: Template file " +
            templateFile +
            " is specified in the Metadata yaml, but it cannot be found or is incorrectly formatted");
    }
}
