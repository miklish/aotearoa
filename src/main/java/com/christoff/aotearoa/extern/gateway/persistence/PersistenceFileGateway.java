package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataIOException;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateIOException;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateResolverFunction;
import com.christoff.aotearoa.intern.gateway.view.IPresenter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PersistenceFileGateway implements IPersistenceGateway
{
    private String _templateDir;
    private String[] _extensions;
    private String _outputDir;
    private String _keystoreMetadataFilename;
    private PersistenceFileHelper _filesysHelp;
    private IPresenter _presenter;

    private static boolean IS_RECURSIVE = false;

    public PersistenceFileGateway(
            String templateFileFolder, List<String> extensions, String outputDir, String keystoreMetadataFilename, IPresenter presenter)
    {
        _templateDir = templateFileFolder;
        _extensions = new String[extensions.size()];
        _extensions = extensions.toArray(_extensions);
        _outputDir = outputDir;
        _keystoreMetadataFilename = PersistenceFileHelper.cleanFilename(keystoreMetadataFilename);
        _filesysHelp = new PersistenceFileHelper();
        _presenter = presenter;
    }

    @Override
    public Object persistValues(TemplateResolverFunction resolver, Map<String, Metadata> allVarMetadata)
        throws TemplateIOException, MetadataException
    {
        // delete target directory's contents, and copy source folder's contents into it
        prepareFolders();

        // add the Keystore Metadata file (if it exists)
        if(_keystoreMetadataFilename != null && !_keystoreMetadataFilename.equals(""))
        {
            File keystoreMetadataFile = new File(_keystoreMetadataFilename);
            if (!keystoreMetadataFile.isFile() || !keystoreMetadataFile.exists())
                throw new MetadataException(
                    "Keystore metadata file " + keystoreMetadataFile.getName() + " either does not exist or is not a file");
        }

        // TODO: Complete the case where we create a new keystore
        //
        File templateFolderFile = new File(_templateDir);
        List<File> files = (List<File>) FileUtils.listFiles(templateFolderFile, _extensions, IS_RECURSIVE);
        for (File file : files)
        {
            // open the template file as a Reader
            PersistenceFileHelper.FileInfo fInfo = null;
            try {
                fInfo = _filesysHelp.getFileInfo(file, false, false, true);
            } catch(MetadataIOException e) {
                _presenter.templateFileNotFoundOrMalformed(fInfo.name);
                continue;
            }

            // use regex replace to inject the actual values
            String resolved = resolver.resolve(fInfo.name, fInfo.reader, allVarMetadata);

            // save the String to the target directory and overwrite the existing value if exists
            String outFilename = PersistenceFileHelper.cleanFilename(_outputDir + "/" + fInfo.name);
            File outFInfo = new File(outFilename);
            
            try {
                FileUtils.writeStringToFile(outFInfo, resolved, (String) null);
            } catch (IOException e) {
                throw new TemplateIOException("Could not resolve template " + outFInfo.getName());
            }
        }
        
        return null;
    }

    
    private void prepareFolders()
        throws TemplateIOException
    {
        // clean target directory and copy contents of source directory into
        // - ensure target and source directories are not the same
        File templateDirFile = new File(_templateDir);
        File outputDirFile = new File(_outputDir);

        if(!templateDirFile.exists() || templateDirFile.isFile())
            throw new TemplateIOException(
                "The specified template folder " +
                templateDirFile.getName() +
                " either does not exist or is not a folder");
        
        if(!outputDirFile.exists() || outputDirFile.isFile())
            throw new TemplateIOException(
                "The specified output folder " +
                outputDirFile.getName() +
                " either does not exist or is not a folder");

        if(templateDirFile.equals(outputDirFile))
            throw new TemplateIOException("Template " + templateDirFile.getName() + " folder and output folder cannot be the same");
        
        // - clean target directory
        try {
            FileUtils.cleanDirectory(outputDirFile);
        } catch (IOException e) {
            throw new TemplateIOException("Could not clean output folder " + templateDirFile.getName());
        }
        
        // - copy source to target
        try {
            FileUtils.copyDirectory(templateDirFile, outputDirFile, null);
        } catch (IOException e) {
            throw new TemplateIOException(
                "Could not copy contents of folder" + templateDirFile.getName() + " to folder " + outputDirFile.getName());
        }
    }

    
    private static String addYamlExt(String f) {
        if(f == null) return null;

        if(f.trim().lastIndexOf('.') < 0)
            return f.trim() + ".yml";
        else
            return f;
    }
}
