package com.christoff.aotearoa.extern.gateway.values;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.values.ValueException;

import java.util.List;
import java.util.Scanner;

public class ValuePromptGateway implements IValueGateway
{
    @Override
    public List<Object> get(Metadata vm)
        throws ValueException
    {
        // inspect variable metadata

        List<String> sMin = vm.getProperty(Metadata.MIN);
        errorCheck(vm.getName(), Metadata.MIN, sMin);
        Integer min = Integer.parseInt(sMin.get(0));

        List<String> sMax = vm.getProperty(Metadata.MAX);
        errorCheck(vm.getName(), Metadata.MAX, sMax);
        Integer max;
        if(sMax.get(0).trim().toLowerCase().equals("inf"))
            max = Integer.MAX_VALUE;
        else
            max = Integer.parseInt(sMax.get(0));

        List<String> sPrompt = vm.getProperty(Metadata.PROMPT_TEXT);
        errorCheck(vm.getName(), Metadata.PROMPT_TEXT, sPrompt);
        String prompt = sPrompt.get(0);

        List<String> sType = vm.getProperty(Metadata.TYPE);
        errorCheck(vm.getName(), Metadata.TYPE, sType);
        String type = sType.get(0);



        // create a scanner so we can read the command-line input
        Scanner scanner = new Scanner(System.in);

        //  prompt for the user's name
        System.out.print("Enter your name: ");

        // get their input as a String
        String username = scanner.next();

        // prompt for their age
        System.out.print("Enter your age: ");

        // get the age as an int
        int age = scanner.nextInt();

        System.out.println(String.format("%s, your age is %d", username, age));

        return null;
    }

    private static void errorCheck(String varName, String paramName, List<String> val)
        throws ValueException
    {
        if(val == null || val.size() == 0) error(varName, paramName);

        for(String s : val)
            if(s == null)
                error(varName, paramName);
    }

    private static void error(String varName, String paramName)
        throws ValueException
    {
        String msg = "No value found for property " + paramName + " in metadata for tag " + varName;
        throw new ValueException(msg);
    }



}
