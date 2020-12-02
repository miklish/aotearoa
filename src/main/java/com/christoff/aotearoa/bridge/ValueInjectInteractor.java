package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.extern.gateway.metadata.KeystoreMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.MetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.TemplateMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.ValueMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.KeystorePersistenceFileGateway;
import com.christoff.aotearoa.extern.gateway.persistence.PersistenceFileGateway;
import com.christoff.aotearoa.extern.gateway.transform.TransformFileGateway;
import com.christoff.aotearoa.extern.gateway.transform.TransformServerGateway;
import com.christoff.aotearoa.extern.gateway.values.*;
import com.christoff.aotearoa.extern.gateway.view.PresenterCLI;
import com.christoff.aotearoa.intern.gateway.metadata.*;
import com.christoff.aotearoa.intern.gateway.persistence.IKeystorePersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.extern.gateway.persistence.TemplateRegexResolver;
import com.christoff.aotearoa.intern.gateway.transform.ITransformGateway;
import com.christoff.aotearoa.intern.gateway.values.IValueGateway;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;

import java.util.*;

public class ValueInjectInteractor
{
    private IMetadataGateway _metadataGateway;
    private ITemplateMetadataGateway _templateMetadataGateway;
    private IValueMetadataGateway _valueMetadataGateway;
    private IKeystoreMetadataGateway _keystoreMetadataGateway;
    private IPersistenceGateway _persistenceGateway;
    private IKeystorePersistenceGateway _keystorePersistenceGateway;
    private IPresenter _presenter;
    private IValueGateway _valueGateway;
    private ITransformGateway _transformGateway;
    private TemplateRegexResolver _regexResolver;
    
    public ValueInjectInteractor(ValueInjectRequest rq)
    {
        boolean usingConfigServer = rq.serverUrl != null;
        boolean usingFileSystemValues = rq.configValsLoc != null;
        boolean usingCustomRegex = rq.regex != null;
    
        /**
         * If no log level switch is on the CLI, default to DEBUG
         * If a log level switch exists with no value, then set to QUIET (no output)
         * If a log level switch with a value exists, then use that value
         */
        String logLevel =
            !rq.logLevelExists ?
                IPresenter.DEBUG.levelId() :
                rq.logLevelValue == null ?
                    IPresenter.QUIET.levelId() :
                    rq.logLevelValue;


        // Construct Gateways
    
        // - Presenter Gateway
        _presenter = new PresenterCLI(logLevel);
    
    
        // - Transform Gateway
        if (usingConfigServer)
            _transformGateway = new TransformServerGateway();
        else
            _transformGateway = new TransformFileGateway(rq.symmetricKey);
        
    
        // - Config File Persistence Gateway
        _persistenceGateway = new PersistenceFileGateway(
            rq.templateDir, rq.templateFileExtensions, rq.outputDir, rq.keystoreMetadataLoc, _presenter);
    
        // - Keystore Persistence Gateway
        _keystorePersistenceGateway = new KeystorePersistenceFileGateway(
            rq.keystoreMetadataLoc, rq.outputDir, _transformGateway);
    

        // - Value Gateway

        // TODO: 1. Build a more generic way of handling the many value types
        // TODO: 2. Determine a way to specify the order of each method

        if (usingFileSystemValues && rq.usingEnvVars && !rq.usingPrompts)
            _valueGateway = new ValueFileEnvironmentGateway(
                new ValueFileGateway(rq.configValsLoc), new ValueEnvironmentGateway());
    
        else if (usingFileSystemValues && !rq.usingEnvVars && !rq.usingPrompts)
            _valueGateway = new ValueFileGateway(rq.configValsLoc);
    
        else if (usingFileSystemValues & rq.usingEnvVars && !rq.usingPrompts)
            _valueGateway = new ValueEnvironmentGateway();

        else if (usingFileSystemValues && !rq.usingEnvVars && rq.usingPrompts)
            _valueGateway = new ValueFilePromptGateway(
                new ValueFileGateway(rq.configValsLoc), new ValuePromptGateway());
    
        else
            _valueGateway = new ValuePromptGateway();
        
        
        // Regex pattern
        if (usingCustomRegex)
            _regexResolver = new TemplateRegexResolver(_presenter, rq.regex);
        else
            _regexResolver = new TemplateRegexResolver(_presenter);

    
        // - Metadata Gateways
        _metadataGateway = new MetadataFileGateway(rq.metadataLoc);

        _templateMetadataGateway = new TemplateMetadataFileGateway(
            _presenter, _regexResolver, rq.templateDir, rq.templateFileExtensions);

        _valueMetadataGateway = new ValueMetadataFileGateway(
            _presenter, rq.configValsLoc);

    
        // - Keystore Metadata Gateway
        _keystoreMetadataGateway = new KeystoreMetadataFileGateway(
            rq.keystoreMetadataLoc, rq.outputDir);
    }

    
    public ValueInjectResponse exec() throws MetadataException
    {
        // Gather all variable metadata
        // TODO: AO2: Need to create default metadata (if not defined in _metadata.yml)
        // TODO: AO2: Need to handle case where _metadata.yml is not used at all
        Map<String, Metadata> allVarMetadata =
            (new MetadataMerge(_presenter)).merge(
                _templateMetadataGateway.getMetadataFromTemplates(),
                _metadataGateway.getMetadata(),
                _valueMetadataGateway.getMetadata());

        // Validate metadata
        for(Metadata vm : allVarMetadata.values()) MetadataValidator.validateMetadata(vm);
        
        // Provide metadata to ValueGateway (some implementations require all metadata at once)
        _valueGateway.setMetadata(allVarMetadata);
    
        // Inject values and transforms into Metadata
        allVarMetadata = new MetadataInjector(_valueGateway, _transformGateway, _presenter).inject(allVarMetadata);

        
        // Resolve and persist the templates
        _presenter.persistingValuesBegin();
        _persistenceGateway.persistValues(_regexResolver::resolve, allVarMetadata);
        _presenter.persistingValuesEnd();
    
        // Check if any metadata went unused
        for(Metadata vm : allVarMetadata.values())
            if(!vm.getUsed()) _presenter.tagDefinedNotUsed(vm.getName());

            
        // Load keystore metadata (if it exists)
        KeystoreMetadataBuilder ksm = new KeystoreMetadataBuilder(
            _keystoreMetadataGateway.getCertificateMap(),
            _keystoreMetadataGateway.getKeystoreMap(),
            _presenter);
        
        if(ksm.getKeystores().size() > 0)
            _keystorePersistenceGateway.persist(ksm.getKeystores());

        
        return new ValueInjectResponse("Success", "SUCCESS");
    }
}
