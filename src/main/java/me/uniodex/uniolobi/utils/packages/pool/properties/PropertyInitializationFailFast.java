package me.uniodex.uniolobi.utils.packages.pool.properties;

import com.zaxxer.hikari.HikariConfig;

public class PropertyInitializationFailFast implements HikariProperty {

    private final boolean value;

    public PropertyInitializationFailFast(boolean value) {
        this.value = value;
    }

	@Override
    public void applyTo(HikariConfig config) {
        config.setInitializationFailFast(value);
    }

}
