package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.ConfigException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.System.exit;

public class ValueInjectCLIStart
{
    private static final String METADATA_LOC = "m";
    private static final String KEYSTORE_METADATA_LOC = "k";
    private static final String TEMPLATE_DIR = "t";
    private static final String CONFIG_VALS_LOC = "v";
    private static final String PROMPTS = "p";
    private static final String ENV_VARS = "e";
    private static final String OUTPUT_DIR = "o";
    private static final String SERVER_URL = "s";
    private static final String REGEX = "r";
    private static final String HELP = "h";
    private static final String SYMMETRIC_KEY = "y";
    private static final String LOG_LEVEL = "l";
    
    public static void main(String[] args)
    {
        // - Parse CLI
        OptionParser optionConfig = configureCommandLineOptions();
        OptionSet optionInput = null;
        try {
            optionInput = optionConfig.parse(args);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            printHelp(optionConfig);
            exit(1);
        }
        if (optionInput.has(HELP)) {
            if (printHelp(optionConfig))
                exit(1);
            else
                exit(0);
        }

        // extract command line values
        /**
         * Options
         *   m / metadata   : variable metadata file (required)
         *   k / kmetadata  : keystore metadata file (optional)
         *   t / templates  : template directory (required)
         *   v / values     : config values file (optional)
         *   p / prompt     : use prompts for values (optional)
         *   e / env        : use environment variables for values or overrides (optional)
         *   o / output     : output directory (required)
         *   s / server     : server url (optional)
         *   r / regex      : regex pattern (optional - default is '\{\{(.*?)\}\}' )
         *   y / key        : symmetric key used to encrypt secrets
         *   l / log        : set logging level
         *   h / help       : help info
         */
        ValueInjectRequest request = new ValueInjectRequest();
        request.configValsLoc = (String) optionInput.valueOf(ValueInjectCLIStart.CONFIG_VALS_LOC);
        request.usingEnvVars = optionInput.has(ValueInjectCLIStart.ENV_VARS);
        request.keystoreMetadataLoc = (String) optionInput.valueOf(ValueInjectCLIStart.KEYSTORE_METADATA_LOC);
        request.metadataLoc = (String) optionInput.valueOf(ValueInjectCLIStart.METADATA_LOC);
        request.outputDir = (String) optionInput.valueOf(ValueInjectCLIStart.OUTPUT_DIR);
        request.serverUrl = (String) optionInput.valueOf(ValueInjectCLIStart.SERVER_URL);
        request.regex = (String) optionInput.valueOf(ValueInjectCLIStart.REGEX);
        request.showHelp = optionInput.has(ValueInjectCLIStart.HELP);
        request.templateDir = (String) optionInput.valueOf(ValueInjectCLIStart.TEMPLATE_DIR);
        request.usingPrompts = optionInput.has(PROMPTS);
        request.symmetricKey = (String) optionInput.valueOf(ValueInjectCLIStart.SYMMETRIC_KEY);
        request.logLevelValue = (String) optionInput.valueOf(ValueInjectCLIStart.LOG_LEVEL);
        request.logLevelExists = optionInput.has(LOG_LEVEL);
        
        boolean usingFileSystemValues = request.configValsLoc != null;
        boolean usingConfigFile = !request.usingPrompts;

        System.out.println("Custom Regex = " + request.regex);


        // - Ensure at least one of prompts, file system, environment config selected
        if (!usingFileSystemValues && !usingConfigFile && !request.usingEnvVars && !request.usingPrompts)
        {
            System.out.println("You must provide values from either a values file, command line prompts, or environment");
            printHelp(optionConfig);
            exit(1);
        }
        
        
        
        ValueInjectResponse resp = null;
        try {
            resp = new ValueInjectInteractor(request).exec();
        } catch(ConfigException c) {
            System.out.println("ERROR: " + c.getMessage());
            exit(1);
        }
        
        // Process response
        if (resp.resultCode.equals(ValueInjectResponse.SUCCESS))
            exit(0);
        else
            exit(1);
    }
    
    private static OptionParser configureCommandLineOptions()
    {
        /**
         * Options
         *   m / metadata   : variable metadata file (required)
         *   k / kmetadata  : keystore metadata file (optional)
         *   t / templates  : template directory (required)
         *   v / values     : config values file (optional)
         *   p / prompt     : use prompts for values (optional)
         *   e / env        : use environment variables for values or overrides (optional)
         *   o / output     : output directory (required)
         *   s / server     : server url (optional)
         *   r / regex      : regex pattern (optional - default is '\{\{(.*?)\}\}' )
         *   y / key        : symmetric key used to encrypt secrets
         *   l / log        : set logging level
         *   h / help       : help info
         */
        OptionParser optionConfig = new OptionParser();
        
        /** variable metadata file */
        final String[] metadataOptions = {METADATA_LOC,"metadata"};
        optionConfig.acceptsAll(
            Arrays.asList(metadataOptions),
            "Variable metadata file (required)").withRequiredArg().required();
        
        /** keystore metadata file */
        final String[] keystoreMetadataOptions = {KEYSTORE_METADATA_LOC,"kmetadata"};
        optionConfig.acceptsAll(
            Arrays.asList(keystoreMetadataOptions),
            "Keystore metadata file (optional)").withRequiredArg();
        
        /** local: template dir */
        final String[] inputdirOptions = {TEMPLATE_DIR,"templates"};
        optionConfig.acceptsAll(
            Arrays.asList(inputdirOptions),
            "Template file folder (required)").withRequiredArg().required();
        
        /** values - file */
        final String[] valsOptions = {CONFIG_VALS_LOC,"values"};
        optionConfig.acceptsAll(
            Arrays.asList(valsOptions),
            "Value file (optional)").withRequiredArg();
        
        /** values - prompts */
        final String[] promptsOptions = {PROMPTS,"prompts"};
        optionConfig.acceptsAll(
            Arrays.asList(promptsOptions),
            "Use command line prompts to enter values (optional)");
        
        /** values - environment variables */
        final String[] envOptions = {ENV_VARS,"env"};
        optionConfig.acceptsAll(
            Arrays.asList(envOptions),
            //"Use environment variables for values or overrides (optional)");  // not yet implemented
            "Get values from environment variables (optional)");
        
        /** server: server url */
        final String[] serverOptions = {SERVER_URL,"server"};
        optionConfig.acceptsAll(
            Arrays.asList(serverOptions),
            "Config Server URL (optional)").withRequiredArg();
    
        /** regex: regex pattern */
        final String[] regexOptions = {REGEX,"regex"};
        optionConfig.acceptsAll(
            Arrays.asList(regexOptions),
            "Regex pattern to locate tags in templates (optional - default is '\\{\\{(.*?)\\}\\}')").withRequiredArg();
        
        /** local: output dir */
        final String[] outputOptions = {OUTPUT_DIR,"outputdir"};
        optionConfig.acceptsAll(
            Arrays.asList(outputOptions),
            "Output directory (required)").withRequiredArg().required();
    
        /** key: symmetric key */
        final String[] symmetricKeyOptions = {SYMMETRIC_KEY,"key"};
        optionConfig.acceptsAll(
            Arrays.asList(symmetricKeyOptions),
            "Symmetric key used to encrypt secrets (optional)").withRequiredArg();
    
        /** log: log level */
        final String[] logLevelOptions = {LOG_LEVEL,"log"};
        optionConfig.acceptsAll(
            Arrays.asList(logLevelOptions),
            "Log output level (optional)").withOptionalArg();
        
        /** help */
        final String[] helpOptions = {HELP,"help"};
        optionConfig.acceptsAll(
            Arrays.asList(helpOptions),
            "Display help/usage information").forHelp();
        
        return optionConfig;
    }
    
    private static boolean printHelp(OptionParser optionConfig)
    {
        try {
            optionConfig.printHelpOn(System.out);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error printing help message");
            return false;
        }
        
        return true;
    }
}
