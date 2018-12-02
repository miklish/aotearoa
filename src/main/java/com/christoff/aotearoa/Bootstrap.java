package com.christoff.aotearoa;

import com.christoff.aotearoa.bridge.ServiceInteractor;
import com.christoff.aotearoa.bridge.ServiceRequest;
import com.christoff.aotearoa.bridge.ServiceResponse;

import com.christoff.aotearoa.intern.gateway.metadata.IVariableMetadataGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.view.IServicePresenter;

import com.christoff.aotearoa.extern.gateway.metadata.local.VariableMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.values.ValueFileGateway;
import com.christoff.aotearoa.extern.gateway.values.ValuePromptGateway;
import com.christoff.aotearoa.extern.gateway.transform.configserver.TransformServerGateway;
import com.christoff.aotearoa.extern.gateway.transform.local.TransformFileGateway;
import com.christoff.aotearoa.extern.view.CLIServicePresenter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.Arrays;
import static java.lang.System.exit;

public class Bootstrap
{
    private static final String METADATA_ID = "d";
    private static final String TEMPLATE_DIR = "i";
    private static final String CONFIG_VALS_ID = "v";
    private static final String OUTPUT_DIR = "o";
    private static final String SERVER_URL = "s";

    public static void main(String[] args)
    {
        new Bootstrap().exec(args);
    }

    public void exec(String[] args)
    {
        // Configure command line
        
        OptionParser optionConfig = configureCommandLineOptions();
        OptionSet optionInput = null;
        try {
            optionInput = parseCommandLine(optionConfig, args);
        } catch(Exception e) {
            System.out.println(e.getMessage());
            printHelp(optionConfig);
            exit(1);
        }
        if(optionInput.has("h")) {
            if(printHelp(optionConfig))
                exit(1);
            else
                exit(0);
        }
        

        // Build Request and invoke Service Interactor
        // - no data put directly in request at the moment
        ServiceRequest request = new ServiceRequest();


        // Construct Gateways for ServiceInteractor
        
        // - Select Presenter Gateway
        IServicePresenter presenter = new CLIServicePresenter();
        // - Select Value Gateway
        IValueGateway valueGateway;
        if(optionInput.has(CONFIG_VALS_ID))
            valueGateway = new ValueFileGateway((String) optionInput.valueOf(CONFIG_VALS_ID));
        else
            valueGateway = new ValuePromptGateway();
        // - Select Transform Gateway
        ITransformGateway transformGateway = null;
        if(!optionInput.has(SERVER_URL))
            transformGateway = new TransformFileGateway();
        else
            transformGateway = new TransformServerGateway();
        // - Select Metadata Gateway
        IVariableMetadataGateway metadataGateway = new VariableMetadataFileGateway(
            (String) optionInput.valueOf(METADATA_ID),
            valueGateway,
            transformGateway);


        // Construct ServiceInteractor
        ServiceInteractor serviceInteractor =
            new ServiceInteractor(metadataGateway, valueGateway, presenter, transformGateway);
        

        // Process response
        ServiceResponse resp = serviceInteractor.exec(request);
        if(resp.resultCode.equals(ServiceResponse.SUCCESS))
            exit(0);
        else
            exit(1);
    }

    private OptionParser configureCommandLineOptions()
    {
        /**
         * Options
         *   d / diff      : diff file  (required)
         *   v / vals      : value file (required in this version -- prompts not yet supported)
         *   i / inputdir  : base directory (required)
         *   o / outputdir : output directory (required)
         *   s / server    ; server url (optional)
         *   h / help      : help info
         */
        OptionParser optionConfig = new OptionParser();

        /** diff file */
        final String[] diffOptions = {METADATA_ID,"diff"};
        optionConfig.acceptsAll(
                Arrays.asList(diffOptions),
                "_diff file (required)").withRequiredArg().required();

        /** local: input dir */
        final String[] inputdirOptions = {TEMPLATE_DIR,"inputdir"};
        optionConfig.acceptsAll(
                Arrays.asList(inputdirOptions),
                "Config file input folder (required)").withRequiredArg().required();

        /** config values */
        final String[] valsOptions = {CONFIG_VALS_ID,"vals"};
        optionConfig.acceptsAll(
                Arrays.asList(valsOptions),
                "Value file (required)").withRequiredArg().required();

        /** local: output dir */
        final String[] outputOptions = {OUTPUT_DIR,"outputdir"};
        optionConfig.acceptsAll(
                Arrays.asList(outputOptions),
                "Output directory (required)").withRequiredArg().required();

        /** local: output dir */
        final String[] serverOptions = {SERVER_URL,"server"};
        optionConfig.acceptsAll(
                Arrays.asList(serverOptions),
                "Config Server URL (optional)").withRequiredArg();

        /** help */
        final String[] helpOptions = {"h","help"};
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
