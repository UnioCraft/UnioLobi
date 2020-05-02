package me.uniodex.uniolobi.utils.packages.pool.properties;

import com.zaxxer.hikari.HikariConfig;

public interface HikariProperty {

    void applyTo(HikariConfig config);

}
