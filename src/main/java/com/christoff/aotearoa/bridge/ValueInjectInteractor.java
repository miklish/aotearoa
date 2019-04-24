package com.christoff.aotearoa.bridge;

import com.christoff.aotearoa.ConfigException;
import com.christoff.aotearoa.extern.gateway.metadata.KeystoreMetadataFileGateway;
import com.christoff.aotearoa.extern.gateway.metadata.MetadataFileGateway;
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
import java.util.regex.PatternSyntaxException;

public class ValueInjectInteractor
{
    private IMetadataGateway _metadataGateway;
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

        
        // Construct Gateways
    
        // - Presenter Gateway
        _presenter = new PresenterCLI();
    
        // - Config File Persistence Gateway
        _persistenceGateway = new PersistenceFileGateway(
            rq.templateDir,
            rq.outputDir,
            rq.keystoreMetadataLoc,
            _presenter);
    
        // - Keystore Persistence Gateway
        _keystorePersistenceGateway = new KeystorePersistenceFileGateway(
            rq.outputDir);
    

        // - Value Gateway

        // TODO: 1. Build a more generic way of handling the many value types
        // TODO: 2. Determine a way to specify the order of each method

        if (usingFileSystemValues && rq.usingEnvVars && !rq.usingPrompts)
            _valueGateway = new ValueFileEnvironmentGateway(
                new ValueFileGateway(rq.configValsLoc),
                new ValueEnvironmentGateway());
    
        else if (usingFileSystemValues && !rq.usingEnvVars && !rq.usingPrompts)
            _valueGateway = new ValueFileGateway(rq.configValsLoc);
    
        else if (usingFileSystemValues & rq.usingEnvVars && !rq.usingPrompts)
            _valueGateway = new ValueEnvironmentGateway();

        else if (usingFileSystemValues && !rq.usingEnvVars && rq.usingPrompts)
            _valueGateway = new ValueFilePromptGateway(
                new ValueFileGateway(rq.configValsLoc),
                new ValuePromptGateway());
    
        else
            _valueGateway = new ValuePromptGateway();
        
        
        // Regex pattern
        if (usingCustomRegex)
            try {
                _regexResolver = new TemplateRegexResolver(rq.regex);
            } catch(PatternSyntaxException e) {
                throw new ConfigException("Custom regex pattern '" + rq.regex + "' is invalid");
            }
        else
            _regexResolver = new TemplateRegexResolver();
        
    
        // - Transform Gateway
        if (usingConfigServer)
            _transformGateway = new TransformServerGateway();
    
        else
            _transformGateway = new TransformFileGateway();
    
        // - Metadata Gateway
        _metadataGateway = new MetadataFileGateway(
            rq.metedataLoc);
    
        // - Keystore Metadata Gateway
        _keystoreMetadataGateway = new KeystoreMetadataFileGateway(
            rq.keystoreMetadataLoc,
            rq.outputDir);
    }


    
    public ValueInjectResponse exec() throws MetadataException
    {
        // Gather all variable metadata
        Map<String, Metadata> allVarMetadata = MetadataBuilder.build(_metadataGateway.getMetadataMap());
        
        // Validate metadata
        for(Metadata vm : allVarMetadata.values()) MetadataValidator.validateMetadata(vm);
        
        // Provide metadata to ValueGateway (some implementations require all metadata at once)
        _valueGateway.setMetadata(allVarMetadata);
    
        // Inject values and transforms into Metadata
        allVarMetadata = new MetadataInjector(_valueGateway, _transformGateway).inject(allVarMetadata);


        
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
