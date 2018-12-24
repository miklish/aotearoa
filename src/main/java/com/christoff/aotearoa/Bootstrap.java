package com.christoff.aotearoa;

import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import com.christoff.aotearoa.extern.gateway.metadata.KeystoreMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.KeystorePersistenceFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileGateway;
import com.christoff.aotearoa.extern.gateway.values.ValueEnvironmentGateway;
import com.christoff.aotearoa.extern.gateway.values.ValueFileEnvironmentGateway;
import com.christoff.aotearoa.intern.gateway.metadata.IKeystoreMetadataGateway;
import com.christoff.aotearoa.intern.gateway.metadata.IMetadataGateway;
import com.christoff.aotearoa.intern.gateway.persistence.IKeystorePersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import com.christoff.aotearoa.extern.gateway.metadata.MetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.values.ValueFileGateway;
import com.christoff.aotearoa.extern.gateway.values.ValuePromptGateway;
import com.christoff.aotearoa.extern.gateway.transform.TransformServerGateway;
import com.christoff.aotearoa.extern.gateway.transform.TransformFileGateway;
import com.christoff.aotearoa.extern.gateway.view.PresenterCLI;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.io.IOException;
import java.util.Arrays;
import static java.lang.System.exit;

public class Bootstrap
{
    private static final String METADATA_ID = "m";
    private static final String KEYSTORE_METADATA_ID = "k";
    private static final String TEMPLATE_DIR = "t";
    private static final String CONFIG_VALS_ID = "v";
    private static final String PROMPTS = "p";
    private static final String ENV_VARS = "e";
    private static final String OUTPUT_DIR = "o";
    private static final String SERVER_URL = "s";
    private static final String HELP = "h";

    
    public static void main(String[] args)
    {
        try {
            new Bootstrap().exec(args);
        } catch(ConfigException c) {
            System.out.println("ERROR: " + c.getMessage());
            exit(1);
        }
    }

    public void exec(String[] args) throws ConfigException
    {
        // Command line
        // - Parse CLI
        OptionParser optionConfig = configureCommandLineOptions();
        OptionSet optionInput = null;
        try {
            optionInput = parseCommandLine(optionConfig, args);
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
    
        // - Collect CLI options
        boolean usingConfigServer = optionInput.has(SERVER_URL);
        boolean usingFileSystemValues = optionInput.has(CONFIG_VALS_ID);
        boolean usingConfigFile = optionInput.has(PROMPTS);
        boolean usingEnvironmentVariables = optionInput.has(ENV_VARS);
    
        // - Ensure at least one of prompts, file system, environment config selected
        if (!usingFileSystemValues && !usingConfigFile && !usingEnvironmentVariables)
        {
            System.out.println("You must provide values from either a values file, command line prompts, or environment");
            printHelp(optionConfig);
            exit(1);
        }
        

        
        // Build Request and invoke Value Injection Interactor
        ValueInjectRequest request = new ValueInjectRequest();


        
        // Construct Gateways
        
        // - Presenter Gateway
        IPresenter presenter = new PresenterCLI();
        
        // - Config File Persistence Gateway
        IPersistenceGateway persistenceGateway = new PersistenceFileGateway(
            (String) optionInput.valueOf(TEMPLATE_DIR),
            (String) optionInput.valueOf(OUTPUT_DIR),
            (String) optionInput.valueOf(KEYSTORE_METADATA_ID));
        
        // - Keystore Persistence Gateway
        IKeystorePersistenceGateway keystorePersistenceGateway = new KeystorePersistenceFileGateway(
            (String) optionInput.valueOf(OUTPUT_DIR));
        
        // - Value Gateway
        IValueGateway valueGateway;
        if (usingFileSystemValues && usingEnvironmentVariables)
            
            valueGateway = new ValueFileEnvironmentGateway(
                new ValueFileGateway((String) optionInput.valueOf(CONFIG_VALS_ID)),
                new ValueEnvironmentGateway());
        
        else if (usingFileSystemValues)
            valueGateway = new ValueFileGateway((String) optionInput.valueOf(CONFIG_VALS_ID));
        
        else if (usingEnvironmentVariables)
            valueGateway = new ValueEnvironmentGateway();
        
        else
            valueGateway = new ValuePromptGateway();

        // - Transform Gateway
        ITransformGateway transformGateway;
        if(usingConfigServer)
            transformGateway = new TransformServerGateway();

        else
            transformGateway = new TransformFileGateway();

        // - Metadata Gateway
        IMetadataGateway metadataGateway = new MetadataFileGateway(
            (String) optionInput.valueOf(METADATA_ID));
        
        // - Keystore Metadata Gateway
        IKeystoreMetadataGateway keystoreMetadataGateway = new KeystoreMetadataFileGateway(
            (String) optionInput.valueOf(KEYSTORE_METADATA_ID),
            (String) optionInput.valueOf(OUTPUT_DIR));


        
        // Construct ValueInjectInteractor
        
        ValueInjectInteractor serviceInteractor =
            new ValueInjectInteractor(
                metadataGateway,
                keystoreMetadataGateway,
                persistenceGateway,
                keystorePersistenceGateway,
                valueGateway,
                transformGateway,
                presenter);
        

        
        // Process response
        
        ValueInjectResponse resp = serviceInteractor.exec(request);
        if (resp.resultCode.equals(ValueInjectResponse.SUCCESS))
            exit(0);
        else
            exit(1);
    }

    private OptionParser configureCommandLineOptions()
    {
        /**
         * Options
         *   m / metadata   : variable metadata file (required)
         *   k / kmetadata : keystore metadata file (optional)
         *   t / templates  : template directory (required)
         *   v / values     : config values file (optional)
         *   p / prompt     : use prompts for values (optional)
         *   e / env        : use environment variables for values or overrides (optional)
         *   o / output     : output directory (required)
         *   s / server     : server url (optional)
         *   h / help       : help info
         */
        OptionParser optionConfig = new OptionParser();
        
        /** variable metadata file */
        final String[] metadataOptions = {METADATA_ID,"metadata"};
        optionConfig.acceptsAll(
                Arrays.asList(metadataOptions),
                "Variable metadata file (required)").withRequiredArg().required();
    
        /** keystore metadata file */
        final String[] keystoreMetadataOptions = {KEYSTORE_METADATA_ID,"kmetadata"};
        optionConfig.acceptsAll(
            Arrays.asList(keystoreMetadataOptions),
            "Keystore metadata file (optional)").withRequiredArg();

        /** local: template dir */
        final String[] inputdirOptions = {TEMPLATE_DIR,"templates"};
        optionConfig.acceptsAll(
                Arrays.asList(inputdirOptions),
                "Template file folder (required)").withRequiredArg().required();

        /** values - file */
        final String[] valsOptions = {CONFIG_VALS_ID,"values"};
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

        /** local: output dir */
        final String[] outputOptions = {OUTPUT_DIR,"outputdir"};
        optionConfig.acceptsAll(
                Arrays.asList(outputOptions),
                "Output directory (required)").withRequiredArg().required();

        /** help */
        final String[] helpOptions = {HELP,"help"};
        optionConfig.acceptsAll(
                Arrays.asList(helpOptions),
                "Display help/usage information").forHelp();

        return optionConfig;
    }
    
    private OptionSet parseCommandLine(OptionParser optionConfig, String[] args) throws RuntimeException
    {
        return optionConfig.parse(args);
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
