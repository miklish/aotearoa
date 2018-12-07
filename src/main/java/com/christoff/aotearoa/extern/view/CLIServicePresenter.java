package com.christoff.aotearoa.extern.view;

import com.christoff.aotearoa.intern.view.IServicePresenter;

public class CLIServicePresenter implements IServicePresenter
{
    @Override
    public void tagDefinedNotUsed(String tagName) {
        System.out.println("WARNING: Tag " + tagName + " defined, but never used");
    }
}
