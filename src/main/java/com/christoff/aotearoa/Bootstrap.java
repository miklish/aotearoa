package com.christoff.aotearoa;

import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import com.christoff.aotearoa.extern.gateway.metadata.KeystoreMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.KeystorePersistenceFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileGateway;
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

    public void exec(String[] args)
        throws ConfigException
    {
        // Configure command line
        
        OptionParser optionConfig = configureCommandLineOptions();
        OptionSet optionInput = null;
        try {
            optionInput = parseCommandLine(optionConfig, args);
        } catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            printHelp(optionConfig);
            exit(1);
        }
        if(optionInput.has(HELP)) {
            if(printHelp(optionConfig))
                exit(1);
            else
                exit(0);
        }
        

        // Build Request and invoke Value Injection Interactor
        // - no data put directly in request at the moment
        ValueInjectRequest request = new ValueInjectRequest();


        // Construct Gateways for ValueInjectInteractor

        // Collect command line options
        boolean usingConfigServer = optionInput.has(SERVER_URL);
        boolean usingFileSystemValues = optionInput.has(CONFIG_VALS_ID);
        boolean usingConfigFile = optionInput.has(PROMPTS);

        // ensure at least one of prompts and file system config selected
        if(!usingFileSystemValues && !usingConfigFile)
        {
            System.out.println("You must provide values from either a values file or from command line prompts");
            printHelp(optionConfig);
            exit(1);
        }

        // - Select Presenter Gateway
        IPresenter presenter = new PresenterCLI();
        
        // - Select Persistence Gateways
        IPersistenceGateway persistenceGateway = new PersistenceFileGateway(
            (String) optionInput.valueOf(TEMPLATE_DIR),
            (String) optionInput.valueOf(OUTPUT_DIR),
            (String) optionInput.valueOf(KEYSTORE_METADATA_ID));
        IKeystorePersistenceGateway keystorePersistenceGateway = new KeystorePersistenceFileGateway(
            (String) optionInput.valueOf(OUTPUT_DIR));
    
        // - Select Value Gateway
        IValueGateway valueGateway;
        if(usingFileSystemValues)
            valueGateway = new ValueFileGateway((String) optionInput.valueOf(CONFIG_VALS_ID));
        else
            valueGateway = new ValuePromptGateway();

        // - Select Transform Gateway
        ITransformGateway transformGateway = null;
        if(usingConfigServer)
            transformGateway = new TransformServerGateway();
        else
            transformGateway = new TransformFileGateway();

        // - Select Metadata Gateways
        IMetadataGateway metadataGateway = new MetadataFileGateway(
            (String) optionInput.valueOf(METADATA_ID));
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
        if(resp.resultCode.equals(ValueInjectResponse.SUCCESS))
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

        /** local: output dir */
        final String[] outputOptions = {OUTPUT_DIR,"outputdir"};
        optionConfig.acceptsAll(
                Arrays.asList(outputOptions),
                "Output directory (required)").withRequiredArg().required();

        /** server: server url */
        final String[] serverOptions = {SERVER_URL,"server"};
        optionConfig.acceptsAll(
                Arrays.asList(serverOptions),
                "Config Server URL (optional)").withRequiredArg();

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

    private static boolean printHelp(OptionParser optionConfig) {
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
