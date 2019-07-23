package com.christoff.aotearoa.intern.gateway.view;

public enum LogLevel
{
    DEBUG   ("debug", "d", 0),
    WARN    ("warn", "w", 10),
    INFO    ("info", "i", 20),
    QUIET   ("quiet", "q", 100);
    
    private final String levelName;
    private final String levelId;
    private final int level;
    
    LogLevel(String levelName, String levelId, int level) {
        this.levelName = levelName;
        this.levelId = levelId;
        this.level = level;
    }
    
    public String levelName() { return levelName; }
    public String levelId() { return levelId; }
    public int level() { return level; }
}
