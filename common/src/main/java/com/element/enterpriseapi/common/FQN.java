package com.element.enterpriseapi.common;

public class FQN {
    private final boolean useDbo;

    public FQN(boolean useDbo) {
        this.useDbo = useDbo;
    }

    public String resolveSybase(String schema, String table) {
        return useDbo ? schema + ".dbo." + table : schema + "." + table;
    }

    public String resolvePostgres(String schema, String table) {
        return schema + "." + table;
    }
}
