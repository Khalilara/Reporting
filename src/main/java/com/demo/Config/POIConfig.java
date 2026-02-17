package com.demo.config;

import org.apache.poi.util.IOUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class POIConfig {
    static {
        // Augmente la limite Apache POI à 200M (au lieu de 100M)
        IOUtils.setByteArrayMaxOverride(200_000_000);
    }
}
