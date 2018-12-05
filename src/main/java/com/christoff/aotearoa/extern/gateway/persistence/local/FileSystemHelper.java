package com.christoff.aotearoa.extern.gateway.persistence.local;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import com.christoff.aotearoa.extern.gateway.YamlHelper;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataException;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataIOException;
import org.apache.commons.io.FileUtils;
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
        public String string = null;
    }

    public FileInfo getFileInfo(String configId, boolean buildYaml, boolean readToString)
        throws
            MetadataException,
            MetadataIOException
    {
        FileInfo info = new FileInfo();
        info.id = configId;
        info.nId = normalize(configId);
        info.file = getFile(info.nId);
        info.exists = info.file.exists();
        info.isFile = info.file.isFile();

        // read in file and render as yaml maps
        if((buildYaml || readToString) && (!info.exists || !info.isFile))
            throw new MetadataIOException("Cannot locate variable metadata file " + info.nId);

        if(info.exists && info.isFile && buildYaml) {
            try {
                info.map = _yamlHelper.loadYaml(info.file);
            } catch (IOException e) {
                throw new MetadataIOException(e.getMessage());
            }
        }

        if(info.exists && info.isFile && readToString) {
            try {
                info.string = FileUtils.readFileToString(info.file, (String) null);
            } catch (IOException e) {
                throw new MetadataIOException(e.getMessage());
            }
        }
        
        return info;
    }
}
