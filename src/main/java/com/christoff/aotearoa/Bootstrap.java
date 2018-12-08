package com.christoff.aotearoa;

import com.christoff.aotearoa.bridge.ValueInjectInteractor;
import com.christoff.aotearoa.bridge.ValueInjectRequest;
import com.christoff.aotearoa.bridge.ValueInjectResponse;
import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileGateway;
import com.christoff.aotearoa.intern.gateway.metadata.IVariableMetadataGateway;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.view.IServicePresenter;
import com.christoff.aotearoa.extern.gateway.metadata.MetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.values.ValueFileGateway;
import com.christoff.aotearoa.extern.gateway.values.ValuePromptGateway;
import com.christoff.aotearoa.extern.gateway.transform.TransformServerGateway;
import com.christoff.aotearoa.extern.gateway.transform.TransformFileGateway;
import com.christoff.aotearoa.extern.view.PresenterCLI;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.io.IOException;
import java.util.Arrays;
import static java.lang.System.exit;

public class Bootstrap
{
    private static final String METADATA_ID = "m";
    private static final String TEMPLATE_DIR = "t";
    private static final String CONFIG_VALS_ID = "v";
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

        // - Select Presenter Gateway
        IServicePresenter presenter = new PresenterCLI();
        
        // - Select Persistence Gateway
        IPersistenceGateway persistenceGateway = new PersistenceFileGateway(
            (String) optionInput.valueOf(TEMPLATE_DIR),
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

        // - Select Metadata Gateway
        IVariableMetadataGateway metadataGateway = new MetadataFileGateway(
            (String) optionInput.valueOf(METADATA_ID));


        // Construct ValueInjectInteractor
        ValueInjectInteractor serviceInteractor =
            new ValueInjectInteractor(metadataGateway, persistenceGateway, valueGateway, transformGateway, presenter);
        

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
         *   t / templates  : template directory (required)
         *   v / values     : config values file (required in this version -- prompts not yet supported)
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

        /** local: template dir */
        final String[] inputdirOptions = {TEMPLATE_DIR,"templates"};
        optionConfig.acceptsAll(
                Arrays.asList(inputdirOptions),
                "Template file folder (required)").withRequiredArg().required();

        /** values */
        final String[] valsOptions = {CONFIG_VALS_ID,"values"};
        optionConfig.acceptsAll(
                Arrays.asList(valsOptions),
                "Value file (required)").withRequiredArg().required();

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
