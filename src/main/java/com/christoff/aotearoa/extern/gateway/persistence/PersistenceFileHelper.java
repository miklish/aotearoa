package com.christoff.aotearoa.extern.gateway.persistence;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import com.christoff.aotearoa.extern.gateway.FileYamlHelper;
import com.christoff.aotearoa.intern.gateway.metadata.MetadataIOException;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.getFile;
import static org.apache.commons.io.FilenameUtils.normalize;

public class PersistenceFileHelper
{
    private FileYamlHelper _yamlHelper;

    public PersistenceFileHelper() {
        _yamlHelper = new FileYamlHelper();
    }

    public class FileInfo
    {
        public String id = null;
        public String nId = null;
        public File file = null;
        public String path = null;
        public String name = null;
        public boolean exists;
        public boolean isFile = true;
        public Map<String, Object> map = null;
        public String string = null;
    }

    /**
     * Normalizes filenames
     * Converts relative filesname to full paths
     *
     * @param filename
     * @return cleaned filename
     */
    public static String cleanFilename(String filename)
    {
        if(filename == null) return filename;

        // try to normalize path
        String cleanFilename = normalize(filename);
        if(cleanFilename == null)                // likely means path is relative
            try {
                cleanFilename = new File(filename).getCanonicalPath();
            } catch (IOException e) {
                throw new MetadataIOException("Cannot locate variable metadata file " + filename);
            }

        return cleanFilename;
    }

    
    public FileInfo getFileInfo(String configId, boolean buildYaml, boolean readToString)
        throws MetadataIOException
    {
        FileInfo info = new FileInfo();
        info.id = configId;
        info.nId = cleanFilename(configId);
        info.file = getFile(info.nId);
        info.exists = info.file.exists();
        info.isFile = info.file.isFile();
        info.name = info.file.getName();
        info.path = info.file.getPath();

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
