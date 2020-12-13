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
    private String _keystoreMetaFilename;
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
        _keystoreMetaFilename = PersistenceFileHelper.cleanFilename(keystoreMetadataFilename);
        _filesysHelp = new PersistenceFileHelper();
        _presenter = presenter;
    }

    @Override
    public Object persistValues(TemplateResolverFunction resolver, Map<String, Metadata> allVarMetadata)
        throws TemplateIOException, MetadataException
    {
        // delete target directory's contents, and copy source folder's contents into it
        prepareFolders();


        // get template folder files
        File templateFolderFile = new File(_templateDir);
        List<File> files = (List<File>) FileUtils.listFiles(templateFolderFile, _extensions, IS_RECURSIVE);


        // add the Keystore Metadata file (if it exists) to the list of files to inject into
        if(_keystoreMetaFilename != null && !_keystoreMetaFilename.equals(""))
        {
            File keystoreMetaFile = new File(_keystoreMetaFilename);
            if (!keystoreMetaFile.isFile() || !keystoreMetaFile.exists())
                throw new MetadataException(
                    "Keystore metadata file " + keystoreMetaFile.getName() + " either does not exist or is not a file");

            // check that keystore file name does not conflict with another file in output folder
            String keystoreMetaName = keystoreMetaFile.getName();

            for(File file : files)
            {
                String canonicalPath;
                try {
                    canonicalPath = file.getCanonicalPath();
                } catch(IOException e) {
                    throw new MetadataIOException("File IO error trying to construct canonical path of " + file.getAbsolutePath());
                }

                // check if filename matches keystore metadata file name, but is not the same file
                if(file.getName().equalsIgnoreCase(keystoreMetaName) && !canonicalPath.equalsIgnoreCase(_keystoreMetaFilename))
                {
                    throw new MetadataException("Keystore Metadata filename " + _keystoreMetaFilename +
                        " conflicts with the name of another file in the template folder");
                }
            }

            // add keystore file to the persisted file list
            files.add(keystoreMetaFile);
        }



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
        {
            // try to create the output file directory
            _presenter.outputLocationLocationDoesNotExist(outputDirFile.getName());

            if(!outputDirFile.mkdir())
                throw new TemplateIOException(
                    "The specified output folder " + outputDirFile.getName() + " could not be created");

            _presenter.outputLocationLocationCreated(outputDirFile.getName());
        }

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
