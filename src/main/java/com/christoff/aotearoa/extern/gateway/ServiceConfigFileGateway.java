package com.christoff.aotearoa.extern.gateway;

import com.christoff.aotearoa.intern.gateway.*;

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

    /***
     *
     * @param baseId
     * @param deleteIfNonEmpty
     * @return Returns true if the base does not exist after method (e.g.: true could mean base was never there)
     */
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
    
    public String getConfigBase(String configId) {
        String id = normalize(configId);
        return getFullPath(id);
    }
    
    public String getConfigName(String configId) {
        String id = normalize(configId);
        return getName(id);
    }
}
