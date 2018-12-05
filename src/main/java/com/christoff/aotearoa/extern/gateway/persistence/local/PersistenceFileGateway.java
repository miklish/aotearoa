package com.christoff.aotearoa.extern.gateway.persistence.local;

import com.christoff.aotearoa.intern.gateway.metadata.VariableMetadata;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateIOException;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.*;

public class PersistenceFileGateway implements IPersistenceGateway
{
    private String _templateDir;
    private String _outputDir;
    private FileSystemHelper _filesysHelp;

    public PersistenceFileGateway(String templateFileFolder, String outputDir) {
        _templateDir = templateFileFolder;
        _outputDir = outputDir;
        _filesysHelp = new FileSystemHelper();
        
    }

    @Override
    public void persistValues(Map<String,VariableMetadata> allVarMetadata)
        throws TemplateIOException
    {
        // delete target directory's contents, and copy source folder's contents into it
        prepareFolders();
        
        
        
        // Collect the set of files in which tags appear
        Set<String> templateFileIds = new HashSet<>();
        for(VariableMetadata varMetadata : allVarMetadata.values()) {
            // extract the file names that the tag appears in
            List<String> configFilenames = varMetadata.getProperty(VariableMetadata.FILES);
            templateFileIds.addAll(configFilenames);
        }

        for(String templateId : templateFileIds)
        {
            // open the template file as a String
            String filename = _templateDir + "/" + addYamlExt(templateId);
            FileSystemHelper.FileInfo fInfo = _filesysHelp.getFileInfo(filename, false, true);
            // - ensure file exists
            if(!fInfo.exists || !fInfo.isFile)
                throw new TemplateIOException("Template " + fInfo.nId + " not found");
    
            // use regex replace to inject the actual values
            String resolved = TemplateResolver.resolve(fInfo.string, allVarMetadata);

            // save the String to the target directory and overrwrite the existing value if exists
            String outFilename = _outputDir + "/" + addYamlExt(templateId);
            FileSystemHelper.FileInfo outFInfo = _filesysHelp.getFileInfo(outFilename, false, false);
            
            try {
                // writeStringToFile(File file, String data, String encoding)
                FileUtils.writeStringToFile(outFInfo.file, resolved, (String) null);
            } catch (IOException e) {
                throw new TemplateIOException("Could not resolve template " + outFInfo.nId);
            }
        }
    }
    
    private void prepareFolders()
        throws TemplateIOException
    {
        // clean target directory and copy contents of source directory into
        // - ensure target and source directories are not the same
        FileSystemHelper.FileInfo templateDirFile = _filesysHelp.getFileInfo(_templateDir, false, false);
        FileSystemHelper.FileInfo outputDirFile = _filesysHelp.getFileInfo(_outputDir, false, false);
        
        if(!templateDirFile.exists || templateDirFile.isFile)
            throw new TemplateIOException(
                "The specified template folder " +
                templateDirFile.nId +
                " either does not exist or is not a folder");
        
        if(!outputDirFile.exists || outputDirFile.isFile)
            throw new TemplateIOException(
                "The specified output folder " +
                outputDirFile.nId +
                " either does not exist or is not a folder");
        
        if(templateDirFile.file.equals(outputDirFile.file))
            throw new TemplateIOException("Template " + templateDirFile.nId + " folder and output folder cannot be the same");
        
        // - clean target directory
        try {
            FileUtils.cleanDirectory(outputDirFile.file);
        } catch (IOException e) {
            throw new TemplateIOException("Could not clean output folder " + templateDirFile.nId);
        }
        
        // - copy source to target
        try {
            FileUtils.copyDirectory(templateDirFile.file, outputDirFile.file, null);
        } catch (IOException e) {
            throw new TemplateIOException(
                "Could not copy contents of folder" + templateDirFile.nId + " to folder " + outputDirFile.nId);
        }
    }
    
    private static String addYamlExt(String f) {
        if(f == null) return null;
        String s = f.trim().toLowerCase();
        if(s.endsWith(".yml"))
            return f;
        else
            return f + ".yml";
    }
}








/*
package com.christoff.aotearoa.extern.gateway;

import com.christoff.aotearoa.intern.gateway.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FilenameUtils.*;

public class ServiceConfigFileGateway implements IServiceConfigDataGateway
{
    private YamlHelper _yamlHelper;

    public ServiceConfigFileGateway()
    {
        _yamlHelper = new YamlHelper();
    }


    private static String getConfigGroupId(String configId) {
        // get first part
        String[] parts = configId.split("/");

        // ensure at least 2 parts
        if(parts.length < 2)
            return null;

        String format = parts[0].trim().toLowerCase();

        if(format.endsWith(".yml"))
            format = format.substring(0,parts[0].length()-4);

        return format;
    }

    private static String getConfigFileName(String configId)
    {
        // get first part
        String[] parts = configId.split("/");

        // ensure at least 2 parts
        if(parts.length < 2)
            return null;

        String format = parts[0].trim().toLowerCase();

        if(!format.endsWith(".yml"))
            return format + ".yml";
        else
            return format;
    }

    private static String getVariableId(String configId)
    {
        // get first part
        String[] parts = configId.split("/");

        // ensure at least 2 parts
        if(parts.length >= 2)
            return Arrays.stream(parts).collect(Collectors.joining("/"));
        else
            return null;
    }


    private class FileInfo
    {
        String id = null;
        String nId = null;
        File file = null;
        boolean exists;
        boolean isFile = true;
        Map<String, Object> map = null;
    }

    private FileInfo getFileInfo(String configId, boolean buildYaml)
    {
        FileInfo info = new FileInfo();
        info.id = configId;
        info.nId = normalize(configId);
        info.file = getFile(info.id);
        info.exists = info.file.exists();
        info.isFile = info.file.isFile();

        if(info.exists && buildYaml) {
            try {
                info.map = _yamlHelper.loadYaml(info.file);
            } catch (IOException e) {
                throw new ConfigDataException(e.getMessage());
            }
        }
        return info;
    }

    public Map<String, Object> get(String configId) {
        return getFileInfo(configId, true).map;
    }

    public Map<String, Object> get(String baseId, String configName) {
        return getFileInfo(FilenameUtils.normalize(baseId + "/" + configName), true).map;
    }

    public boolean save(Map<String, Object> map, String baseId, String configName, boolean deleteIfExists) {
        return save(map, baseId + "/" + configName, deleteIfExists);
    }

    public boolean save(Map<String, Object> map, String configId, boolean deleteIfExists)
    {
        FileInfo info = getFileInfo(configId, true);

        if(info.exists) {
            if (deleteIfExists)
                info.file.delete();
            else
                throw new ConfigAlreadyExists(
                    "Config data with normalized id " + info.nId + " already exists and connect be deleted");
        }

        try {
            _yamlHelper.dump(map, info.nId);
        } catch (IOException e) {
            throw new ConfigIOException(e.getMessage());
        }

        return true;
    }

    public boolean configExists(String configId) {
        String id = normalize(configId);
        File file = getFile(id);
        return file.isFile() && file.exists();
    }

    public boolean configExists(String baseId, String configName) {
        return configExists(baseId + "/" + configName);
    }

    public boolean baseExists(String baseId) {
        String id = normalize(baseId);
        File file = getFile(id);
        return file.isDirectory() && file.exists();
    }

    public boolean createBase(String baseId)
    {
        String id = normalize(baseId);
        File file = getFile(id);

        if(file.exists() && file.isDirectory())
            return file.mkdir();
        else
            return false;
    }

    private static boolean dirEmpty(File file)
    {
        File parentDir = file.getParentFile();
        if(parentDir.isDirectory() && parentDir.list().length == 0)
            return true;
        else
            return true;
    }
*/

/*
    public boolean deleteBase(String baseId, boolean deleteIfNonEmpty)
    {
        String id = normalize(baseId);
        File base = getFile(id);

        if(!base.isDirectory()) return false;

        if(base.exists())
        {
            if((!dirEmpty(base) && deleteIfNonEmpty) || dirEmpty(base))
                try {
                    deleteDirectory(base);
                    return true;
                } catch (IOException e) {
                    throw new ConfigIOException(e.getMessage());
                }
            else
                return false;
        }
        else
            return true;
        }
    }

    public String getConfigBase(String configId) {
        String id = normalize(configId);
        return getFullPath(id);
    }

    public String getConfigName(String configId) {
        String id = normalize(configId);
        return getName(id);
    }
}
*/