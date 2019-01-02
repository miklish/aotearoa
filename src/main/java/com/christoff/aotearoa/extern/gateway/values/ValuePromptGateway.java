package com.christoff.aotearoa.extern.gateway.values;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.values.ValueException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ValuePromptGateway implements IValueGateway
{
    private static final String QUIT = "\\\\q";
    private static final String COMPLETE_MULTIPLE = "\\\\n";
    private static final String DEFAULT = "";
    
    @Override
    public void setMetadata(Map<String, Metadata> allVarMetadata)
    {
        // Display explanation on how to quit
        System.out.println("Type " + QUIT + " to quit. Enter a blank line to use default (if default exists)");
        
        // display defaults
        // add default option to metadata
        // add option to select project
    }

    @Override
    public List<Object> get(Metadata vm)
        throws ValueException
    {
        // Parse metadata
        Integer min = Integer.parseInt(vm.getProperty(Metadata.MIN).get(0));
        String sMax = vm.getProperty(Metadata.MAX).get(0).trim().toLowerCase();
        Integer max = sMax.equals("inf") ? Integer.MAX_VALUE : Integer.parseInt(sMax);
        String prompt = vm.getProperty(Metadata.PROMPT_TEXT).get(0);
        String type = vm.getProperty(Metadata.TYPE).get(0);
        String dflt = vm.getProperty(Metadata.DEFAULTS) == null ? null : vm.getProperty(Metadata.DEFAULTS).get(0);


        Scanner scanner = new Scanner(System.in).useDelimiter(" ");
        List<Object> retVal = new LinkedList<>();

        boolean isDflt = dflt != null;
        String dfltPrompt = isDflt ? " : (" + dflt + ")" : "";

        // get the variable type
        if(min > 1 || max > 1)
        {
            System.out.println(
                "\n" +
                "Next entry requires between " + min + " and " + sMax + " values. Enter " + COMPLETE_MULTIPLE + " line to complete");
            System.out.println("[" + prompt + dfltPrompt + "]: ");
    
            for(int i = 0; i < max.intValue(); ++i)
            {
                System.out.print("[Value " + (i+1) + "]: ");
                
                // get their input as a String
                String value = scanner.nextLine().split(" ")[0];
                if(value.equals(COMPLETE_MULTIPLE)) break;
        
                if(value.equals(QUIT))
                    throw new ValueException("User exited");
        
                if(isDflt && value.equals(DEFAULT))
                {
                    vm.setDefaultUsed();
                    retVal.add(dflt);
                    System.out.println(" >> value selected is '" + dflt + "'");
                }
                else if(!isDflt && value.equals(DEFAULT)) {
                    System.out.println("  ! There is no default for this variable! Try again !");
                    --i;
                    continue;
                }
                else
                    retVal.add(value);
            }
            System.out.println();
        }
        else {
            while(true)
            {
                System.out.print("[" + prompt + dfltPrompt + "]: ");

                // get their input as a String
                String value = scanner.nextLine();

                if (value.equals(QUIT))
                    throw new ValueException("User exited");

                if (isDflt && value.equals(DEFAULT))
                {
                    vm.setDefaultUsed();
                    retVal.add(dflt);
                    System.out.println(" >> value selected is '" + dflt + "'");
                }
                else if (!isDflt && value.equals(DEFAULT)) {
                    System.out.println("  ! There is no default for this variable. Try again !");
                    continue;
                } else
                    retVal.add(value);

                break;
            }
        }

        return retVal;
    }
}
