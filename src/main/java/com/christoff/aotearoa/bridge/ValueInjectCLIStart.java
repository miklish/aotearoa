package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.ConfigException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class ValueInjectCLIStart
{
    private static final String METADATA_LOC = "m";
    private static final String KEYSTORE_METADATA_LOC = "k";
    private static final String TEMPLATE_DIR = "t";
    private static final String TEMPLATE_FILE_EXTS = "x";
    private static final String CONFIG_VALS_LOC = "v";
    private static final String PROMPTS = "p";
    private static final String ENV_VARS = "e";
    private static final String OUTPUT_DIR = "o";
    private static final String SERVER_URL = "s";
    private static final String REGEX = "r";
    private static final String HELP = "h";
    private static final String SYMMETRIC_KEY = "y";
    private static final String BUILD_SAFE = "b";
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
            System.exit(1);
        }


        // extract command line values
        /**
         * Options
         *   m / metadata   : variable metadata file (optional)
         *   k / kmetadata  : keystore metadata file (optional)
         *   t / templates  : template directory (required)
         *   x / extension  : template file extension (required)
         *   v / values     : config values file (optional)
         *   p / prompt     : use prompts for values (optional)
         *   e / env        : use environment variables for values or overrides (optional)
         *   o / output     : output directory (required)
         *   s / server     : server url (optional)
         *   r / regex      : regex pattern (optional - default is '\{\{(.*?)\}\}' )
         *   y / key        : symmetric key used to encrypt secrets
         *   l / loglevel   : set logging level
         *   b / buildsafe  : disable forced exit of AO2 to allow use in build automation tools (optional)
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
        request.templateFileExtensions = (List<String>) optionInput.valuesOf(ValueInjectCLIStart.TEMPLATE_FILE_EXTS);
        request.usingPrompts = optionInput.has(PROMPTS);
        request.symmetricKey = (String) optionInput.valueOf(ValueInjectCLIStart.SYMMETRIC_KEY);
        request.isBuildSafe = optionInput.has(ValueInjectCLIStart.BUILD_SAFE);
        request.logLevelValue = (String) optionInput.valueOf(ValueInjectCLIStart.LOG_LEVEL);
        request.logLevelExists = optionInput.has(LOG_LEVEL);
        
        boolean usingFileSystemValues = request.configValsLoc != null;
        boolean usingConfigFile = !request.usingPrompts;


        // Print help message if user requests it
        if (optionInput.has(HELP))
        {
            if (printHelp(optionConfig)) {      // Note: JOpt's Help Printing option can throw an exception
                exit(0, request);
                return;
            }
            else {
                exit(1, request);
                return;
            }
        }


        // - Ensure at least one of prompts, file system, environment config selected
        if (!usingFileSystemValues && !usingConfigFile && !request.usingEnvVars && !request.usingPrompts)
        {
            System.out.println("You must provide values from either a values file, command line prompts, or environment");
            printHelp(optionConfig);
            exit(1, request);
            return;
        }
        
        
        ValueInjectResponse resp = null;
        try {
            resp = new ValueInjectInteractor(request).exec();
        } catch(ConfigException c) {
            System.out.println("ERROR: " + c.getMessage());
            exit(1, request);
        }
        
        // Process response
        if (resp.resultCode.equals(ValueInjectResponse.SUCCESS))
            exit(0, request);
        else
            exit(1, request);
    }

    private static void exit(int status, ValueInjectRequest rq) {
        if(!rq.isBuildSafe) System.exit(status);
    }
    
    private static OptionParser configureCommandLineOptions()
    {
        /**
         * Options
         *   m / metadata   : variable metadata file (optional)
         *   k / kmetadata  : keystore metadata file (optional)
         *   t / templates  : template directory (required)
         *   x / extension  : template file extension (required)
         *   v / values     : config values file (optional)
         *   p / prompt     : use prompts for values (optional)
         *   e / env        : use environment variables for values or overrides (optional)
         *   o / output     : output directory (required)
         *   s / server     : server url (optional)
         *   r / regex      : regex pattern (optional - default is '\{\{(.*?)\}\}' )
         *   y / key        : symmetric key used to encrypt secrets
         *   l / loglevel   : set logging level
         *   b / buildsafe  : disable forced exit of AO2 to allow use in build automation tools (optional)
         *   h / help       : help info
         */
        OptionParser optionConfig = new OptionParser();
        
        /** variable metadata file */
        final String[] metadataOptions = {METADATA_LOC,"metadata"};
        optionConfig.acceptsAll(
            Arrays.asList(metadataOptions),
            "Variable metadata file (optional)").withRequiredArg();
        
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

        /** local: template file extensions */
        final String[] templateFileExtensionOptions = {TEMPLATE_FILE_EXTS,"extension"};
        optionConfig.acceptsAll(
            Arrays.asList(templateFileExtensionOptions),
            "Template file extensions (e.g.: -x xml -x yml) (required)").withRequiredArg().required();
        
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

        /** local: buildsafe */
        final String[] buildSafeOptions = {BUILD_SAFE,"buildsafe"};
        optionConfig.acceptsAll(
            Arrays.asList(buildSafeOptions),
            "Disable forced exit of AO2 to allow use in build automation tools (optional)");
    
        /** log: log level */
        final String[] logLevelOptions = {LOG_LEVEL,"loglevel"};
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
