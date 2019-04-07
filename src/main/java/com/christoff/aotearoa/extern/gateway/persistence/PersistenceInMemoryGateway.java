package com.christoff.aotearoa.extern.gateway.persistence;

import com.christoff.aotearoa.intern.gateway.metadata.Metadata;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataIOException;
import com.christoff.aotearoa.intern.gateway.persistence.IPersistenceGateway;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateIOException;
import com.christoff.aotearoa.intern.gateway.persistence.TemplateResolverFunction;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PersistenceInMemoryGateway implements IPersistenceGateway
{
    private String _templateDir;
    private String _keystoreMetadataFilename;
    private PersistenceFileHelper _filesysHelp;

    public PersistenceInMemoryGateway(String templateFileFolder, String keystoreMetadataFilename)
    {
        _templateDir = templateFileFolder;
        _keystoreMetadataFilename = keystoreMetadataFilename;
        _filesysHelp = new PersistenceFileHelper();
    }

    @Override
    public Object persistValues(TemplateResolverFunction resolver, Map<String, Metadata> allVarMetadata)
        throws TemplateIOException
    {
        // Collect the set of files in which tags appear
        Set<String> templateFileIds = new HashSet<>();
        for(Metadata vm : allVarMetadata.values()) {
            // extract the file names that the tag appears in
            List<String> configFilenames = vm.getProperty(Metadata.FILES);
            templateFileIds.addAll(configFilenames);
        }

        
        // add the Keystore Metadata file (if it exists)
        if(_keystoreMetadataFilename != null && !_keystoreMetadataFilename.equals(""))
        {
            File keystoreMetadataFile = new File(_keystoreMetadataFilename);
            if (!keystoreMetadataFile.isFile() || !keystoreMetadataFile.exists())
                throw new MetadataException(
                    "Keystore metadata file " + keystoreMetadataFile.getName() + " either does not exist or is not a file");
            
            templateFileIds.add(keystoreMetadataFile.getName());
        }

        // TODO: Complete the case where we create a new keystore
        //
        for(String templateId : templateFileIds)
        {
            // open the template file as a String
            String filename = _templateDir + "/" + addYamlExt(templateId);
            PersistenceFileHelper.FileInfo fInfo = null;
            try {
                fInfo = _filesysHelp.getFileInfo(filename, true, false);
            } catch(MetadataIOException e) {
                throw new TemplateIOException("Template file " + addYamlExt(templateId) + " is specified in the " +
                        "Metadata yaml, but it cannot be found or is incorrectly formatted");
            }

            // TODO: convert file to YAML
            Map yaml = fInfo.map;
            
            // TODO: use dfs and inject leaf nodes with tags with their values
            //
            
            
            
            // use regex replace to inject the actual values
            String resolved = resolver.resolve(fInfo.file.getName(), fInfo.string, allVarMetadata);
            
        }
        
        return null;
    }
    
    private static String addYamlExt(String f) {
        if(f == null) return null;

        if(f.trim().lastIndexOf('.') < 0)
            return f.trim() + ".yml";
        else
            return f;
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
    private FileYamlHelper _yamlHelper;

    public ServiceConfigFileGateway()
    {
        _yamlHelper = new FileYamlHelper();
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