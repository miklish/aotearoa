package com.christoff.aotearoa.extern.gateway;

import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class FileYamlHelper
{
    private Yaml _yaml;
    private DumperOptions _options;

    public FileYamlHelper() {
        _options = new DumperOptions();
        _options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        _options.setPrettyFlow(true);
        _yaml = new Yaml(_options);
    }

    public Map<String,Object> loadYaml(File file) throws IOException {
        return _yaml.load(FileUtils.openInputStream(file));
    }

    public Map<String,Object> loadYaml(String filename) throws IOException {
        return _yaml.load(FileUtils.openInputStream(FileUtils.getFile(filename)));
    }
}
