package me.uniodex.uniolobi.utils.packages.pool.properties;

import com.zaxxer.hikari.HikariConfig;

public class PropertyCatalog implements HikariProperty {

    private final String value;

    public PropertyCatalog(String value) {
        this.value = value;
    }

    @Override
    public void applyTo(HikariConfig config) {
        config.setCatalog(value);
    }

}
