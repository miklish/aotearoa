package com.christoff.aotearoa;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.Arrays;

import static java.lang.System.exit;

public class Bootstrap
{
    public static void main(String[] args)
    {
        OptionSet options = null;
        try {
            options = parseArgs(args);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            exit(1);
        }
        
        System.out.printf(
            "diff file = %s\n" +
                "inputdir = %s\n" +
                (options.has("v") ? options.valueOf("v") : "no value file specified") + "\n" +
                "outputdir = %s\n",
            options.valueOf("d"),
            options.valueOf("i"),
            options.valueOf("o"));
        
        exit(0);
    }
    
    private static OptionSet parseArgs(String[] args) throws RuntimeException
    {
        /**
         * Options
         *   d / diff      : diff file  (required)
         *   i / inputdir  : base directory (required)
         *   v / vals      : value file (optional)
         *   o / outputdir : output directory (required)
         *   h / help      : help info
         */
        final OptionParser optionParser = new OptionParser();
    
        final String[] diffOptions = {"d","diff"};
        optionParser.acceptsAll(Arrays.asList(diffOptions), "_diff file (required)").withRequiredArg().required();
    
        final String[] inputdirOptions = {"i","inputdir"};
        optionParser.acceptsAll(Arrays.asList(inputdirOptions), "Config file input folder (required)").withRequiredArg().required();
    
        final String[] valsOptions = {"v","vals"};
        optionParser.acceptsAll(Arrays.asList(valsOptions), "Value file (optional)").withRequiredArg();
    
        final String[] outputOptions = {"o","outputdir"};
        optionParser.acceptsAll(Arrays.asList(outputOptions), "Output directory (required)").withRequiredArg().required();
    
        final String[] helpOptions = {"h","help"};
        optionParser.acceptsAll(Arrays.asList(helpOptions), "Display help/usage information").forHelp();
    
        return optionParser.parse(args);
    }
}
