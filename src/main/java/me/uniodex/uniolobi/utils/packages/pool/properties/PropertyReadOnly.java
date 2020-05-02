package me.uniodex.uniolobi.utils.packages.pool.properties;

import com.zaxxer.hikari.HikariConfig;

public class PropertyReadOnly implements HikariProperty {

    private final boolean value;

    public PropertyReadOnly(boolean value) {
        this.value = value;
    }

    @Override
    public void applyTo(HikariConfig config) {
        config.setReadOnly(value);
    }

}
