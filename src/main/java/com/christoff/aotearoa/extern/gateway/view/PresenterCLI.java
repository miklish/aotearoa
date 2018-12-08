package com.christoff.aotearoa.extern.gateway.view;

import com.christoff.aotearoa.intern.gateway.view.IServicePresenter;

public class PresenterCLI implements IServicePresenter
{
    @Override
    public void tagDefinedNotUsed(String tagName) {
        System.out.println("WARNING: Tag " + tagName + " defined, but never used");
    }
}
