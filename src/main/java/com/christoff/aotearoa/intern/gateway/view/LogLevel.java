package com.christoff.aotearoa.intern.gateway.view;

public enum LogLevel
{
    TRACE   ("trace", "t", 0),
    DEBUG   ("debug", "d", 10),
    WARN    ("warn", "w", 20),
    INFO    ("info", "i", 30),
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
