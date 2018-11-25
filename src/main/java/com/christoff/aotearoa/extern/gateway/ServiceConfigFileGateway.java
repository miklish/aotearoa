package com.christoff.aotearoa.extern.gateway;

import com.christoff.aotearoa.intern.gateway.IServiceConfigDataGateway;

import java.util.Map;

public class ServiceConfigFileGateway implements IServiceConfigDataGateway
{
    public Map<String, Object> get(String configId) {
        return null;
    }
    
    public String save(Map<String, Object> config, String baseId, String configName, boolean deleteIfExists) {
        return null;
    }
    
    public String save(Map<String, Object> config, String configId, boolean deleteIfExists) {
        return null;
    }
    
    public boolean checkConfigExists(String configId) {
        return false;
    }
    
    public boolean checkConfigExists(String baseId, String configName) {
        return false;
    }
    
    public boolean checkConfigBaseExists(String baseId) {
        return false;
    }
    
    public boolean createBase(String baseId) {
        return false;
    }
    
    public boolean deleteBase(String baseId, boolean deleteIfNonEmpty) {
        return false;
    }
}
