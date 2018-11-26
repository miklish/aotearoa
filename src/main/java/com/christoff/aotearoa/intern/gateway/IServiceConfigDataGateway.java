package com.christoff.aotearoa.intern.gateway;

import java.util.Map;

public interface IServiceConfigDataGateway
{
    public Map<String, Object> get(String configId);
    public Map<String, Object> get(String baseId, String configName);
    public boolean save(Map<String, Object> map, String baseId, String configName, boolean deleteIfExists);
    public boolean save(Map<String, Object> map, String configId, boolean deleteIfExists);
    public boolean configExists(String configId);
    public boolean configExists(String baseId, String configName);
    public boolean baseExists(String baseId);
    public boolean createBase(String baseId);
    public boolean deleteBase(String baseId, boolean deleteIfNonEmpty);
}
