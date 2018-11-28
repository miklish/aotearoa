package com.christoff.aotearoa;

import com.christoff.aotearoa.bridge.ServiceInteractor;
import com.christoff.aotearoa.bridge.ServiceRequest;
import com.christoff.aotearoa.bridge.ServiceResponse;
import com.christoff.aotearoa.extern.gateway.DefaultTransformGateway;
import com.christoff.aotearoa.extern.gateway.ServiceConfigFileGateway;
import com.christoff.aotearoa.extern.gateway.ServiceValueFileGateway;
import com.christoff.aotearoa.extern.gateway.ServiceValuePromptGateway;
import com.christoff.aotearoa.extern.view.CLIServicePresenter;
import com.christoff.aotearoa.intern.gateway.IServiceConfigDataGateway;
import com.christoff.aotearoa.intern.gateway.IServiceValueGateway;
import com.christoff.aotearoa.intern.gateway.ITransformGateway;
import com.christoff.aotearoa.intern.view.IServicePresenter;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.System.exit;

public class Bootstrap
{
    private static final String CONFIG_ID = "d";
    private static final String BASE_ID = "i";
    private static final String CONFIG_VALS_ID = "v";
    private static final String OUTPUT_BASE_ID = "o";

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
        
        ServiceRequest request = new ServiceRequest();
        request.baseId = (String) optionInput.valueOf(BASE_ID);
        request.configId = (String) optionInput.valueOf(CONFIG_ID);
        request.outputBaseId = (String) optionInput.valueOf(OUTPUT_BASE_ID);
        

        // Construct Gateways for ServiceInteractor
        
        // - Select Presenter Gateway
        IServicePresenter presenter = new CLIServicePresenter();
        // - Select Config Data Gateway
        IServiceConfigDataGateway configGateway = new ServiceConfigFileGateway();
        // - Select Config Value Gateway
        IServiceValueGateway valueGateway;
        if(optionInput.has(CONFIG_VALS_ID))
            valueGateway = new ServiceValueFileGateway((String) optionInput.valueOf(CONFIG_VALS_ID));
        else
            valueGateway = new ServiceValuePromptGateway();
        // - Select Transform Gateway
        ITransformGateway transformGateway = new DefaultTransformGateway();
        

        // Construct ServiceInteractor
        ServiceInteractor serviceInteractor =
            new ServiceInteractor(configGateway, valueGateway, presenter, transformGateway);
        

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
         *   i / inputdir  : base directory (required)
         *   v / vals      : value file (required in this version -- prompts not yet supported)
         *   o / outputdir : output directory (required)
         *   h / help      : help info
         */
        OptionParser optionConfig = new OptionParser();

        final String[] diffOptions = {CONFIG_ID,"diff"};
        optionConfig.acceptsAll(Arrays.asList(diffOptions), "_diff file (required)").withRequiredArg().required();

        final String[] inputdirOptions = {BASE_ID,"inputdir"};
        optionConfig.acceptsAll(Arrays.asList(inputdirOptions), "Config file input folder (required)").withRequiredArg().required();

        final String[] valsOptions = {CONFIG_VALS_ID,"vals"};
        optionConfig.acceptsAll(Arrays.asList(valsOptions), "Value file (optional)").withRequiredArg().required();

        final String[] outputOptions = {OUTPUT_BASE_ID,"outputdir"};
        optionConfig.acceptsAll(Arrays.asList(outputOptions), "Output directory (required)").withRequiredArg().required();

        final String[] helpOptions = {"h","help"};
        optionConfig.acceptsAll(Arrays.asList(helpOptions), "Display help/usage information").forHelp();

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
