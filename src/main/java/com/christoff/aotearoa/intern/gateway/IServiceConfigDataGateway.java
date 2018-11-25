package com.christoff.aotearoa.intern.gateway;

import java.util.Map;

public interface IServiceConfigDataGateway
{
    public Map<String, Object> get(String configId);
    public String save(Map<String, Object> config, String baseId, String configName, boolean deleteIfExists);
    public String save(Map<String, Object> config, String configId, boolean deleteIfExists);
    public boolean checkConfigExists(String configId);
    public boolean checkConfigExists(String baseId, String configName);
    public boolean checkConfigBaseExists(String baseId);
    public boolean createBase(String baseId);
    public boolean deleteBase(String baseId, boolean deleteIfNonEmpty);
}
