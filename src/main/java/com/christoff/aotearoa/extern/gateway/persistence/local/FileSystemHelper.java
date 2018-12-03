package com.christoff.aotearoa.extern.gateway.persistence.local;

import com.christoff.aotearoa.extern.gateway.metadata.local.VariableMetadataFileGateway;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataFormatException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.christoff.aotearoa.extern.gateway.YamlHelper;

import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FilenameUtils.normalize;

public class FileSystemHelper
{
    private YamlHelper _yamlHelper;

    public FileSystemHelper() {
        _yamlHelper = new YamlHelper();
    }

    public class FileInfo
    {
        public String id = null;
        public String nId = null;
        public File file = null;
        public boolean exists;
        public boolean isFile = true;
        public Map<String, Object> map = null;
    }

    public FileInfo getFileInfo(String configId, boolean buildYaml)
    {
        FileInfo info = new FileInfo();
        info.id = configId;
        info.nId = normalize(configId);
        info.file = getFile(info.nId);
        info.exists = info.file.exists();
        info.isFile = info.file.isFile();

        if(info.exists && buildYaml) {
            try {
                info.map = _yamlHelper.loadYaml(info.file);
            } catch (IOException e) {
                throw new MetadataFormatException(e.getMessage());
            }
        }
        return info;
    }
}
